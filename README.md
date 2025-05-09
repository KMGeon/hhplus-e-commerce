## 항해플러스 - 이커머스 프로젝트

### 설계 문서
1. [요구사항 분석](/docs/1.intro.md)
2. [시퀀스 다이어그램](/docs/2.SequenceDiagram.md)
3. [상태 다이어그램](/docs/4.classDiagram.md)
4. [ERD 문서](/docs/3.erd.md)


### 인프라 구성

1. Docker 실행 
```shell
   docker-compose -f docker-compose.yml up -d
```
2. Redis Sentinel, HAProxy, MySQL
3. HAProxy는 Redis Sentinel을 통해 Redis Master/Slave를 모니터링합니다. (http://localhost/haproxy_stats)
4. HaProxy는 Redis Master/Slave에 대한 요청을 라우팅합니다.

```mermaid
flowchart TB
    client([클라이언트]) --> haproxy
    
    subgraph redis_network[Docker 네트워크]
        haproxy(HAProxy\n포트: 80, 9000, 5001) --> sentinel1
        haproxy --> sentinel2
        haproxy --> sentinel3
        
        subgraph sentinel_cluster[Sentinel 클러스터]
            sentinel1(sentinel-1)
            sentinel2(sentinel-2)
            sentinel3(sentinel-3)
        end
        
        sentinel1 -.모니터링.-> master
        sentinel2 -.모니터링.-> master
        sentinel3 -.모니터링.-> master
        
        sentinel1 -.모니터링.-> slave1
        sentinel1 -.모니터링.-> slave2
        sentinel1 -.모니터링.-> slave3
        
        sentinel2 -.모니터링.-> slave1
        sentinel2 -.모니터링.-> slave2
        sentinel2 -.모니터링.-> slave3
        
        sentinel3 -.모니터링.-> slave1
        sentinel3 -.모니터링.-> slave2
        sentinel3 -.모니터링.-> slave3
        
        subgraph redis_cluster[Redis 클러스터]
            master(Redis Master) --> slave1(Redis Slave 1)
            master --> slave2(Redis Slave 2)
            master --> slave3(Redis Slave 3)
        end
    end
    
    subgraph independent[독립 서비스]
        mysql[(MySQL\n포트: 3306\nDB: hhplus)]
    end
    
    haproxy -.-> master
    haproxy -.-> slave1
    haproxy -.-> slave2
    haproxy -.-> slave3
    
    classDef proxy fill:#f9a,stroke:#333,stroke-width:2px
    classDef redis fill:#f6d5a4,stroke:#333,stroke-width:1px
    classDef sentinel fill:#a4c2f6,stroke:#333,stroke-width:1px
    classDef db fill:#d5f6a4,stroke:#333,stroke-width:1px
    
    class haproxy proxy
    class master,slave1,slave2,slave3 redis
    class sentinel1,sentinel2,sentinel3 sentinel
    class mysql db
```