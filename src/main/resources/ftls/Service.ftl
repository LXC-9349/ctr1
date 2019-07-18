package ${BasePackageName}${ServicePackageName};

import ${BasePackageName}${EntityPackageName}.${ClassName};
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author ${Author}
 * @date  ${Date}
 */
public interface  ${ClassName}Service {

    void searchPage(ResponseData responseData, PageMode pageMode, ${ClassName} ${EntityName});

    ${ClassName} get(Integer id);

    List<${ClassName}> findList(${ClassName} ${EntityName});

    List<${ClassName}> findAllList(${ClassName} ${EntityName});

    int insert(${ClassName} ${EntityName});

    int insertBatch(List<${ClassName}> ${EntityName}s);

    int update(${ClassName} ${EntityName});

    boolean delete(Integer id);

}
