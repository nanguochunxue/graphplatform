package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Created by chengangxiong on 2019/03/22
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_env_file")
@NoArgsConstructor
public class DcEnvFilePo extends BasePo {

    @Column(name = "env_id")
    private Long envId;

    @NotNull
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "content", length = 50)
    private byte[] content;

    public DcEnvFilePo(MultipartFile file) throws IOException {
        this.name = file.getOriginalFilename();
        this.content = file.getBytes();
    }
}
