package com.dreamfactory.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

import com.dreamfactory.model.AppGroupRequest;
public class AppGroupsRequest {
  /* Array of system application group records. */
  @JsonProperty("record")
  private List<AppGroupRequest> record = new ArrayList<AppGroupRequest>();
  /* Array of system application group record identifiers, used for batch GET, PUT, PATCH, and DELETE. */
  @JsonProperty("ids")
  private List<Integer> ids = new ArrayList<Integer>();
  public List<AppGroupRequest> getRecord() {
    return record;
  }
  public void setRecord(List<AppGroupRequest> record) {
    this.record = record;
  }

  public List<Integer> getIds() {
    return ids;
  }
  public void setIds(List<Integer> ids) {
    this.ids = ids;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppGroupsRequest {\n");
    sb.append("  record: ").append(record).append("\n");
    sb.append("  ids: ").append(ids).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

