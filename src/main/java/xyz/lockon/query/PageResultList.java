package xyz.lockon.query;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 分页查询的结果
 * @param <T> 数据类型
 */
@Data
@Builder
public class PageResultList<T> {
    private List<T> dataList;

    private long totalRecordNum;

    private long currentPage;

}
