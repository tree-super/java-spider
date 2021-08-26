package net.hneb.jxetyy.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshuai
 */
@Data
@NoArgsConstructor
public class ZtreeView {

    private Long id;

    private Long pId;

    private String name;

    private boolean open;

    private boolean checked = false;

    public ZtreeView(Long id, Long pId, String name, boolean open) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.open = open;
    }
}
