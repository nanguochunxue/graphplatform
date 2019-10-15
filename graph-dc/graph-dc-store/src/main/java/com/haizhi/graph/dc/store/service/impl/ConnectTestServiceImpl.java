package com.haizhi.graph.dc.store.service.impl;

import com.google.common.base.Preconditions;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.service.DcEnvService;
import com.haizhi.graph.dc.core.service.EnvFileCacheService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.dc.store.service.ConnectTestService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EnvVersion;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.arango.client.ArangoClient;
import com.haizhi.graph.server.gp.client.GpClient;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import com.haizhi.graph.server.hdfs.HDFSHelper;
import com.haizhi.graph.server.hive.HiveConfig;
import com.haizhi.graph.server.hive.HiveHelper;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.haizhi.graph.sys.file.service.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by chengangxiong on 2019/03/30
 */
@Service
public class ConnectTestServiceImpl implements ConnectTestService {

    private static final GLog LOG = LogFactory.getLogger(ConnectTestServiceImpl.class);

    private ExecutorService connectTestThreadPool = Executors.newFixedThreadPool(3);

    @Autowired
    private DcEnvService dcEnvService;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private EnvFileCacheService envFileCacheService;

    @Autowired
    private EsIndexDao esIndexDao;

    @Autowired
    private ArangoClient arangoClient;

    @Autowired
    private HBaseClient hBaseClient;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private RestService restService;

    @Autowired
    private GpClient gpClient;

    @Override
    public Response<String> process(DcStoreSuo dcStoreSuo) {
        String url = dcStoreSuo.getUrl();
        String user = dcStoreSuo.getUser();
        String password = dcStoreSuo.getPassword();
        Long envId = dcStoreSuo.getEnvId();
        StoreType storeType = dcStoreSuo.getType();
        if (storeType.isHadoopPlatform()) {
            return testHadoop(url, envId, storeType, user, password);
        } else {
            if (storeType == StoreType.ES) {
                if(StringUtils.isNotEmpty(user.trim())||StringUtils.isNoneEmpty(password.trim())){
                    return Response.error("elasticsearch test connect:username and password is unnecessary, please leave nothing!");
                }
                return getTestElasticsearchResult(url, envId);
            } else if (storeType == StoreType.GDB) {
                return testArangodb(url, user, password);
            } else if (storeType == StoreType.Greenplum) {
                return testGreenplum(url, user, password);
            } else {
                return Response.success("not supported storeType :" + storeType);
            }
        }
    }

    @Override
    public Response<String> autoGenUrl(Long envId, StoreType storeType) {
        try {
            DcEnvPo dcEnv = dcEnvService.findOne(envId);
            if (Objects.isNull(dcEnv)) {
                return Response.success();
            }
            if (!storeType.isHadoopPlatform()) {
                return Response.success();
            }
            StoreURL storeURL = storeUsageService.findStoreURL(envId);
            if (storeType == StoreType.Hbase) {
                String url = hBaseClient.autoGenUrl(storeURL);
                return Response.success(url);
            }
            if (storeType == StoreType.HDFS) {
                //String url = HDFSHelper.autoGenURL(storeURL.getFilePath());
                String url = "";
                if (url.equals("file:///")) {
                    return Response.success();
                }
                return Response.success(url);
            }
        } catch (Exception e) {
            LOG.warn("auto gen url for envId=" + envId + ", storeType=" + storeType + " got exception", e);
            return Response.success();
        }
        return Response.success();
    }

    public Response extractExceptionMsg(Exception e) {
        String mainMsg = e.getMessage();
        return e.getCause() == null ? Response.error(mainMsg) : e.getCause().getMessage() == null ? Response.error
                (mainMsg) : Response.error(mainMsg + "[" + e.getCause().getMessage() + "]");
    }

    private Response<String> testArangodb(String url, String user, String password) {
        try {
            submitAndWaitTimeout(() -> {
                arangoClient.testConnect(url, user, password);
            });
        } catch (Exception e) {
            LOG.error(e);
            return extractExceptionMsg(e);
        }
        return Response.success();
    }

    private void testHive(String url, String user, String password, String userPrincipal, Map<String, String>
            filePath) {
        HiveConfig hiveConfig = new HiveConfig(url, user, password, false, userPrincipal, filePath);
        try (HiveHelper hiveHelper = new HiveHelper(hiveConfig)) {
            hiveHelper.testConnect();
        }
    }

    private Response<String> testHadoop(String url, Long envId, StoreType storeType, String user, String password) {
        try {
            Runnable runnable = null;
            Map<String, String> row;
            switch (storeType) {
                case Hbase:
                    if (Objects.isNull(envId)) {
                        runnable = () -> testHBase(url, false, user, new HashMap<>(), EnvVersion.CDH.name());
                    } else {
                        DcEnvPo env = dcEnvService.findOne(envId);
                        Preconditions.checkNotNull(env);
                        row = envFileCacheService.findEnvFile(envId);
                        SysDictPo dictPo = sysDictService.findById(env.getVersionDictId());
                        String envVersion = Objects.nonNull(dictPo) ? dictPo.getValue() : EnvVersion.CDH.name();
                        boolean securityEnabled = Constants.Y.equals(env.getSecurityEnabled());
                        if (securityEnabled) {
                            if (envVersion.toLowerCase().startsWith(EnvVersion.FI.name())) {
                                if (!row.containsKey("core-site.xml") ||
                                        !row.containsKey("hdfs-site.xml") ||
                                        !row.containsKey("hbase-site.xml") ||
                                        !row.containsKey("user.keytab") ||
                                        !row.containsKey("krb5.conf")) {
                                    throw new IllegalArgumentException(
                                            "missing core-site.xml or hdfs-site.xml or hbase-site.xml or user.keytab " +
                                                    "or " +
                                                    "krb5.conf");
                                }
                            } else if (envVersion.toLowerCase().startsWith(EnvVersion.KSYUN.name())) {
                                if (!row.containsKey("hbase-site.xml") ||
                                        !row.containsKey("user.keytab") ||
                                        !row.containsKey("krb5.conf")) {
                                    throw new IllegalArgumentException(
                                            "missing hbase-site.xml or user.keytab or krb5.conf");
                                }
                            }
                        }
                        runnable = () -> testHBase(url, securityEnabled, env.getUser(), row, envVersion);
                    }
                    break;
                case Hive:
                    row = envFileCacheService.findEnvFile(envId);
                    runnable = () -> testHive(url, user, password, user, row);
                    break;
                case HDFS:
                    row = envFileCacheService.findEnvFile(envId);
                    runnable = () -> testHDFS(url, row);
                    break;
                case ES:
                    runnable = () -> testElasticsearch(url, envId);
                    break;
            }
            submitAndWaitTimeout(runnable);
            return Response.success();
        } catch (Exception e) {
            LOG.error(e);
            return extractExceptionMsg(e);
        }
    }

    private void testHDFS(String url, Map<String, String> row) {
        HDFSHelper hdfsHelper = new HDFSHelper(url, row);
        hdfsHelper.testConnect();
    }

    private void testHBase(String url, boolean securityEnabled, String user, Map<String, String> row, String
            envVersion) {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl(url);
        storeURL.setSecurityEnabled(securityEnabled);
        storeURL.setUserPrincipal(user);
        storeURL.setEnvVersion(envVersion);
        storeURL.getFilePath().putAll(row);
        hBaseClient.testConnect(storeURL);
    }

    private void submitAndWaitTimeout(Runnable callable) {
        Future future = connectTestThreadPool.submit(callable);
        try {
            future.get(10, TimeUnit.SECONDS);
            future.cancel(true);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            LOG.error(ex);
            throw new RuntimeException("test connect fail, please check your input", ex);
        }
    }

    private void testElasticsearch(String url, Long envId) {
        try {
            submitAndWaitTimeout(() -> {
                StoreURL storeURL = new StoreURL();
                storeURL.setUrl(url);
                if (!Objects.isNull(envId)) {
                    DcEnvPo env = dcEnvService.findOne(envId);
                    Preconditions.checkNotNull(env);
                    Map<String, String> row = envFileCacheService.findEnvFile(envId);
                    boolean securityEnabled = Constants.Y.equals(env.getSecurityEnabled());
                    if (securityEnabled) {
                        if (!row.containsKey("user.keytab") ||
                                !row.containsKey("krb5.conf")) {
                            throw new IllegalArgumentException("missing user.keytab or krb5.conf");
                        }
                    }
                    storeURL.setUserPrincipal(env.getUser());
                    storeURL.setSecurityEnabled(securityEnabled);
                    storeURL.getFilePath().putAll(row);
                }
                esIndexDao.testConnect(storeURL);
            });
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    private Response getTestElasticsearchResult(String url, Long envId) {
        try {
            testElasticsearch(url, envId);
            return Response.success();
        } catch (Exception e) {
            LOG.error(e);
            return extractExceptionMsg(e);
        }
    }

    private Response<String> testGreenplum(String url, String user, String password) {
        try {
            submitAndWaitTimeout(() -> gpClient.testConnect(url, user, password));
            return Response.success();
        } catch (Exception e) {
            LOG.error(e);
            return extractExceptionMsg(e);
        }
    }

    @Override
    public Response<String> gpExportUrl(String exportUrl) {
        Map<String, String> parameterMap = new HashMap<>();
        try {
            Response resp = restService.doGet(exportUrl, parameterMap, Response.class);
            if (resp.getPayload().getData().equals("GP_SERVER_RUNNING")) {
                return Response.success();
            } else {
                return Response.error("not a gp server");
            }
        } catch (Exception e) {
            LOG.error("test connect fail:" + exportUrl, e);
            return Response.error("test connect fail:" + e.getMessage());
        }
    }
}
