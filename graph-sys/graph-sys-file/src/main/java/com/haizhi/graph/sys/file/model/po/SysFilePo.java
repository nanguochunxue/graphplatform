package com.haizhi.graph.sys.file.model.po;

import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.sys.file.constant.StoreType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Data
@Entity
@Table(name = "sys_file")
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysFilePo extends BasePo {

    @NotNull
    @Column(name = "name", length = 50)
    private String name;

    @NotNull
    @Column(name = "url", length = 200)
    private String url;

    @NotNull
    @Column(name = "store_type", length = 30)
    @Enumerated(EnumType.STRING)
    private StoreType storeType;

    @NotNull
    @Column(name = "file_size", length = 15)
    private Long fileSize;

    public SysFilePo(String name, Long fileSize, String url, StoreType storeType) {
        this.name = name;
        this.fileSize = fileSize;
        this.url = url;
        this.storeType = storeType;
    }
}
