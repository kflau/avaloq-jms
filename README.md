# avaloq-jms

## Getting Started

Run below command to build this project.

```xml
mvn clean install
```

## Prerequisite

Follow these two steps to install an IBM MQ in linux environment with below configurations.

1. [Install][link-1]
2. [Verify][link-2]
3. Add user mquser, oracle, mqm to group mqm 

```yaml
ibm.mq:
  queueManager: QM1
  channel: DEV.ADMIN.SVRCONN
  connName: <host>(1414)
  user: mquser
  password: mquser
```

[link-1]: https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_9.1.0/com.ibm.mq.ins.doc/q008640_.htm
[link-2]: https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_9.1.0/com.ibm.mq.ins.doc/q009243_.htm
