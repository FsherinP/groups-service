package org.sunbird.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.exception.BaseException;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.models.Group;
import org.sunbird.response.Response;
import org.sunbird.util.DBUtil;
import org.sunbird.util.JsonKey;

public class GroupDaoImpl implements GroupDao {
  private static final String GROUP_TABLE_NAME = "group";

  private CassandraOperation cassandraOperation = ServiceFactory.getInstance();
  private ObjectMapper mapper = new ObjectMapper();
  private static GroupDao groupDao = null;

  public static GroupDao getInstance() {
    if (groupDao == null) {
      groupDao = new GroupDaoImpl();
    }
    return groupDao;
  }

  @Override
  public String createGroup(Group groupObj) throws BaseException {

    Map<String, Object> map =
        mapper.convertValue(groupObj, new TypeReference<Map<String, Object>>() {});
    map.put(JsonKey.CREATED_ON, new Timestamp(Calendar.getInstance().getTime().getTime()));
    // need to fix , as mapper is converting set to arrayList
    map.put(JsonKey.ACTIVITIES, groupObj.getActivities());
    cassandraOperation.insertRecord(DBUtil.KEY_SPACE_NAME, GROUP_TABLE_NAME, map);
    return (String) map.get(JsonKey.ID);
  }

  @Override
  public Response readGroup(String groupId) throws BaseException {
    Response responseObj =
        cassandraOperation.getRecordById(DBUtil.KEY_SPACE_NAME, GROUP_TABLE_NAME, groupId);
    return responseObj;
  }

  @Override
  public Response readGroups(List<String> groupIds) throws BaseException {
    Response responseObj =
        cassandraOperation.getRecordsByPrimaryKeys(
            DBUtil.KEY_SPACE_NAME, GROUP_TABLE_NAME, groupIds, JsonKey.ID);
    return responseObj;
  }

  @Override
  public Response updateGroup(Group groupObj) throws BaseException {
    Map<String, Object> map = mapper.convertValue(groupObj, Map.class);
    map.put(JsonKey.UPDATED_ON, new Timestamp(Calendar.getInstance().getTime().getTime()));
    Response responseObj =
        cassandraOperation.updateRecord(DBUtil.KEY_SPACE_NAME, GROUP_TABLE_NAME, map);
    return responseObj;
  }
}
