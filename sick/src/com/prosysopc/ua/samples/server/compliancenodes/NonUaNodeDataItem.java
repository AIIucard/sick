/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.StatusCode;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.StatusCodes;

public class NonUaNodeDataItem {

  private NodeId dataType;
  private final NonUaNodeComplianceNodeManager manager;
  private final String name;
  private StatusCode status = new StatusCode(StatusCodes.Bad_WaitingForInitialData);
  private DateTime timestamp;
  private Object value;

  public NonUaNodeDataItem(NonUaNodeComplianceNodeManager manager, String name) {
    this.manager = manager;
    this.name = name;
  }

  public NodeId getDataType() {
    return dataType;
  }

  public void getDataValue(DataValue dataValue) {
    dataValue.setValue(new Variant(getValue()));
    dataValue.setStatusCode(getStatus());
    dataValue.setServerTimestamp(DateTime.currentTime());
    dataValue.setSourceTimestamp(timestamp);
  }

  public String getName() {
    return name;
  }

  public StatusCode getStatus() {
    return status;
  }

  public DateTime getTimestamp() {
    return timestamp;
  }

  public Object getValue() {
    return value;
  }

  public void setDataType(NodeId dataType) {
    this.dataType = dataType;
  }

  public void setValue(Object value) {
    setValue(value, StatusCode.GOOD);
  }

  public void setValue(Object value, StatusCode status) {
    if (status == null) {
      status = StatusCode.BAD;
      manager.notifyMonitoredDataItems(this);
    }
    if ((this.value != value) || !this.status.equals(status)) {
      this.value = value;
      this.status = status;
      this.timestamp = DateTime.currentTime();
      manager.notifyMonitoredDataItems(this);
    }

  }
}
