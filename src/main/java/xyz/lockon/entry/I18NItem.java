package xyz.lockon.entry;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class I18NItem {
    private String zhCN;
    private String enUS;
    private String localLang;
}
