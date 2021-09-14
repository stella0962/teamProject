# Online Apply for Classes 

# 온라인 수강신청 시스템

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전체 구현과제를 구성한 예제입니다.
이는 Cloud Native Application의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [온라인 수강신청 시스템](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [개발 운영 환경 분리](#개발-운영-환경-분리)
    - [모니터링](#모니터링)

# 서비스 시나리오


기능적 요구사항
1. 강사가 Online 강의를 등록/수정/삭제 등 강의를 관리한다
2. 수강생은 강사가 등록한 강의를 조회한 후 듣고자 하는 강의를 신청한다
3. 수강생은 신청한 강의에 대한 강의료를 결제한다
4. 강의 수강 신청이 완료되면 강의 교재를 배송한다
5. 강의 수강 신청을 취소하면 강의 교재 배송을 취소한다
6. 강의 수강 신청 내역을 언제든지 수강 신청자가 볼 수 있다

비기능적 요구사항
1. 트랜잭션
    1. 강의 결제가 완료 되어야만 수강 신청 완료 할 수 있음 Sync. 호출
    
2. 장애격리
    1. 수강신청 시스템이 과중되면 사용자를 잠시동안 받지 않고 신청을 잠시 후에 하도록 유도한다  Circuit breaker
    
3. 성능
    1. 학생은 마이페이지에서 등록된 강의와 수강 및 교재 배송 상태를 확인할 수 있어야 한다  CQRS
    2. 수강신청/배송 상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven


# 체크포인트

- 분석 설계
  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://www.msaez.io/#/storming/vmQZPdUuSNhfyj62lkXveg5urp72/15622ad452dd80b8131472a7e471686b


### 이벤트 도출
![이벤트 도출](https://user-images.githubusercontent.com/49930207/131055626-88773fdf-77a1-4ac7-a25e-9b200fa565b1.png)

### Actor, Command 추가
![Actor, Command](https://user-images.githubusercontent.com/49930207/131058387-035d27bd-c2ff-4c08-a04a-60d361920ef6.png)

### Aggregate으로 묶기
![Aggregate](https://user-images.githubusercontent.com/49930207/131058844-53e7785f-1dd2-49e0-9bd4-1ad30af43527.png)

    - 수강신청, 강의등록, 결제내역, 배송내역 업무영역 단위로 묶음

### Bounded Context로 묶기

![Bounded Context](https://user-images.githubusercontent.com/49930207/131061428-9b1f1367-97bf-448c-b3e7-70cdcdc442af.png)

    - 도메인 서열 분리 
        - Core Domain:  class, course : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 class 의 경우 1주일 1회 미만, course 의 경우 1개월 1회 미만
        - General Domain:   pay : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 (핑크색으로 이후 전환할 예정)

### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![Policy & Req/Resp & Pub/Sub](https://user-images.githubusercontent.com/49930207/131070356-6fbd5fbc-5b17-41a8-bcde-ca3388166f75.png)
        - 서비스 간의 관계 확인
      
### 완성된 1차 모형

![완성된 1차 모형](https://user-images.githubusercontent.com/49930207/131074402-af4a05e9-c044-4159-8b9b-26631735f05d.png)

    - 서비스 간의 관계 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/49930207/131092558-df5ef712-257b-4aa4-9b49-908db9f13013.png)
    
    - 수강생이 강의를 신청한다 (ok)
    - 수강생이 강의를 결제한다 (ok)
    - 강의신청이 되면 주문 내역이 배송팀에게 전달된다 (ok)
    - 배송팀에서 강의 교재 배송 출발한다 (ok)

![image](https://user-images.githubusercontent.com/49930207/131092741-a78344ba-3b13-4ed6-a42d-b0ed005727b3.png)
    
    - 수강생이 강의를 취소할 수 있다 (ok) 
    - 강의가 취소되면 결제 취소된다 (ok) 
    - 결제가 취소되면 배송이 취소된다 (ok) 


### 모델 완료
![image](https://user-images.githubusercontent.com/49930207/131438677-e65d348a-775e-4121-9982-f723e1cbb1c7.png)

    - 마이크로서비스별 Aggregate , Evnecnt, Policy, Command Attribute 정의
    - Naming변경
    - Mypage CQRS 정의

### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/49930207/131438968-8c4913fa-1bf3-4461-8586-234cfa657c2f.png)

트랜잭션
강의 결제가 완료 되어야만 수강 신청 완료 할 수 있음 Sync 호출
장애격리
수강신청 시스템이 과중되면 사용자를 잠시동안 받지 않고 신청을 잠시 후에 하도록 유도한다 Circuit breaker
성능
학생은 마이페이지에서 등록된 강의와 수강 및 교재 배송 상태를 확인할 수 있어야 한다 CQRS

1. 강의 결제가 완료 되어야만 수강 신청 완료 할 수 있음 Sync 호출 및 장애격리
  - 수강생 주문시 결제처리: 결제가 완료되지 않은 주문은 절대 받지 않는다는 경영자의 오랜 신념에 따라, ACID 트랜잭션 적용. 주문완료 시 결제처리에 대해서는 Request-Response & Circuit Breaker 적용
  
2. 성능 Async. 호출 (Event Driven 방식)
  - 결제 완료시 배송처리: pay에서 course 마이크로서비스로 주문요청이 전달되는 과정에 있어서 Store 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
 - 나머지 모든 inter-microservice 트랜잭션: 주문상태, 배달상태 등 모든 이벤트에 대해 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.
3. 성능 Mypage (CQRS)
  - 학생용 마이페이지 및 강사용 마이페이지를 각각 구성하여 언제든 상태 정보를 확인 할 수 있음 CQRS



## 헥사고날 아키텍처 다이어그램 도출

![image](https://user-images.githubusercontent.com/80744192/121182134-bf4f5480-c89d-11eb-8015-76c4761c8ed6.png)


  - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
  - 호출관계에서 PubSub 과 Req/Resp 를 구분함
  - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 Sping-Boot로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd course
mvn spring-boot:run

cd class
mvn spring-boot:run 

cd payment
mvn spring-boot:run  

cd gateway
mvn spring-boot:run

cd delivery
mvn spring-boot:run

cd mypage
mvn spring-boot:run
```

- 아래 부터는 AWS 클라우드의 EKS 서비스 내에 서비스를 모두 배포 후 설명을 진행한다.
```
root@labs-1409824742:/home/project/team/lecture/course/kubernetes# kubectl get all
NAME                           READY   STATUS    RESTARTS   AGE
pod/alert-7cbc74668-clsdv      2/2     Running   0          3h13m
pod/class-5864b4f7cc-rzrz9     1/1     Running   0          163m
pod/course-64978c8dd8-nmwxp    1/1     Running   0          112m
pod/gateway-65d7888594-mqpls   1/1     Running   0          3h11m
pod/pay-575875fc9-kk56d        1/1     Running   2          162m
pod/siege                      1/1     Running   0          8h

NAME                 TYPE           CLUSTER-IP       EXTERNAL-IP                                                                  PORT(S)          AGE
service/alert        ClusterIP      10.100.108.57    <none>                                                                       8084/TCP         6h43m
service/class        ClusterIP      10.100.233.190   <none>                                                                       8080/TCP         7h12m
service/course       ClusterIP      10.100.121.125   <none>                                                                       8080/TCP         3h30m
service/gateway      LoadBalancer   10.100.138.145   aa8ed367406254fc0b4d73ae65aa61cd-24965970.ap-northeast-2.elb.amazonaws.com   8080:31881/TCP   8h
service/kubernetes   ClusterIP      10.100.0.1       <none>                                                                       443/TCP          9h
service/pay          ClusterIP      10.100.76.173    <none>                                                                       8080/TCP         7h4m

NAME                      READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/alert     1/1     1            1           3h13m
deployment.apps/class     1/1     1            1           163m
deployment.apps/course    1/1     1            1           3h12m
deployment.apps/gateway   1/1     1            1           3h11m
deployment.apps/pay       1/1     1            1           162m

NAME                                 DESIRED   CURRENT   READY   AGE
replicaset.apps/alert-7cbc74668      1         1         1       3h13m
replicaset.apps/class-5864b4f7cc     1         1         1       163m
replicaset.apps/course-64978c8dd8    1         1         1       3h12m
replicaset.apps/gateway-65d7888594   1         1         1       3h11m
replicaset.apps/pay-575875fc9        1         1         1       162m

NAME                                        REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/class   Deployment/class   0%/30%    1         10        1          155m
horizontalpodautoscaler.autoscaling/pay     Deployment/pay     0%/30%    1         10        1          155m
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: 
 (예시는 payment 마이크로 서비스). 이때 가능한 중학교 수준의 영어를 사용하려고 노력했다. 

```
package team;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String applyId;
    private String payMethod;
    private String payAccount;
    private String payStaus;
    private String addr;
    private String telephoneInfo;
    private String studentName;

    @PostPersist
    public void onPostPersist() {
        PaymentAppoved paymentAppoved = new PaymentAppoved();
        BeanUtils.copyProperties(this, paymentAppoved);
        paymentAppoved.setPayStaus("PaymentAprroved");
        paymentAppoved.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        PaymentCanceled paymentCanceled = new PaymentCanceled();
        BeanUtils.copyProperties(this, paymentCanceled);
        paymentCanceled.publishAfterCommit();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getPayStaus() {
        return payStaus;
    }

    public void setPayStaus(String payStaus) {
        this.payStaus = payStaus;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getTelephoneInfo() {
        return telephoneInfo;
    }

    public void setTelephoneInfo(String telephoneInfo) {
        this.telephoneInfo = telephoneInfo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

```
package team;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

    List<Payment> findByApplyId(String applyId);
}
```

- 적용 후 REST API 의 테스트

```
//강의 등록
http POST http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses name=korean teacher=Hong-Gil-dong fee=10000 textBook=kor_book openYn=false

//강의 등록 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses

//강의 스케쥴 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules

//강의료 수정
http PATCH http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses/1 fee=125000

//강의료 수정 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses

//My page 확인 (교사용) 강의료 수정 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/mypages

//수강 신청
http POST http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes courseId=1 fee=12500 student=Kim-Soon-hee textBook=kor_book

//수강 등록 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes

//결제 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/payments

//강의 스케줄 Open 확인 (Open여부 : true 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules

//강의 Open 확인 (Open여부 : true 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses

//배송 시작 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/deliveries

//수강 취소
http DELETE http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes/1

//수강 취소 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes

//강의 스케줄 Close 확인 (Open여부 : false 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules

//강의 Close 확인 (Open여부 : false 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses

//결제 취소 확인 (상태값 "CANCEL" 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/payments

//배송 취소 확인 (상태값 "DELIVERY_CANCEL" 확인)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/deliveries

//My page 확인 (학생용)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/inquiryMypages

//My page 확인 (교사용)
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/mypages

```

## 폴리글랏 퍼시스턴스

Delivery 서비스 (schedule) 는 mysql 을 사용하여 구현하였다. 
Spring Cloud JPA를 사용하여 개발하였기 때문에 소스의 변경 부분은 전혀 없으며, 단지 데이터베이스 제품의 설정 (pom.xml, application.yml) 만으로 mysql 에 부착시켰다

```
# pom.yml (Delivery)

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>8.0.25</version>
		</dependency>
		<dependency>

```

```
# application.yml (Delivery)

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/class?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: Sktngm12@@
  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    generate-ddl: true
    show-sql: true

```

- mysql 서비스 확인 (kubectl get all,pvc -n mysql)

```
NAME                         READY   STATUS    RESTARTS   AGE
pod/mysql-7b794c7595-7zfp5   1/1     Running   0          147m

NAME            TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/mysql   ClusterIP   10.100.133.120   <none>        3306/TCP   147m

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/mysql   1/1     1            1           147m

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/mysql-7b794c7595   1         1         1       147m

NAME                          STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
persistentvolumeclaim/mysql   Bound    pvc-37f946de-9321-43d7-b935-ffe6c6125587   8Gi        RWO            gp2            147m
```
- mysql client 에서 테스트한 데이터 확인
```
root@ubuntu:/# mysql -h mysql.mysql.svc.cluster.local -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1840
Server version: 5.7.30 MySQL Community Server (GPL)

Copyright (c) 2000, 2021, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> use scheduledb
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+-----------------------+
| Tables_in_scheduledb  |
+-----------------------+
| delivery_table        |
| hibernate_sequence    |
+-----------------------+
2 rows in set (0.00 sec)

mysql> select * from course_schedule_table;
+----+-----------+-------------+---------+---------------+---------------+
| id | course_id | course_name | open_yn | student_count | teacher       |
+----+-----------+-------------+---------+---------------+---------------+
|  1 |         1 | korean      |        |             2 | Hong-Gil-dong |
|  2 |         3 | korean      |         |             0 | Hong-Gil-dong |
+----+-----------+-------------+---------+---------------+---------------+
2 rows in set (0.00 sec)

mysql> 
```

## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 강의신청(class)->결제(payment) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# (class) PaymentService.java

package classnew.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

import feign.Feign;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;

@FeignClient(name="payment", url="http://localhost:8083", configuration=PaymentService.PaymentServiceConfiguration.class, fallback=PaymentServiceFallback.class)
public interface PaymentService {
    @RequestMapping(method= RequestMethod.POST, path="/payments", consumes = "application/json") //payments로 해야 데이터insert
    public boolean payApprove(@RequestBody Payment payment);
```

- FallBack 처리
```
# (schedule) PaymentServiceFallback.java

package classnew.external;

import org.springframework.stereotype.Component;

@Component
public class PaymentServiceFallback implements PaymentService {
    @Override
    //public Long payApprove(Long classId){
    public boolean payApprove(Payment payment) {

        //do nothing if you want to forgive it

        System.out.println("\\n=========FALL BACK STARTING=========\\n"); //fallback 메소드 작동 테스트
       //return 0L;
        return false;
    }
}
```

- 수강신청등록/취소가 발생할 때마다 학생수를 Count 하다가 학생수가 이 되면 강좌 폐강 또는 학생이 한명이상 이면 강좌 오픈되도록 처리. (@PreUpdate)
```
# (schedule) CourseSchedule.java
    @PreUpdate
    public void onPreUpdate() {
        System.out.println(
                "\n\n##### CourseSchedule onPreUpdate : " + this.getPreOpenYn() + "/" + this.getOpenYn() + "\n\n");

        if (this.getPreOpenYn().booleanValue() != this.getOpenYn().booleanValue()) {
            Course course = new Course();
            // mappings goes here
            course.setOpenYn(this.getOpenYn());

            if (!ScheduleApplication.applicationContext.getBean(CourseService.class).course(course,
                    this.getCourseId().toString())) {
                throw new RollbackException("Failed during Course Open");
            }
        }
    }
```
- 강의 Service 에 강의 오픈 유무 Update 
```
# (course) CourseController.java

    @PutMapping(value = "/modifyOpenYn/{id}")
    public boolean modifyOpenYn(@RequestBody Map<String, String> param, @PathVariable String id) {
        Course course = null;
        boolean result = false;

        System.out.println("\n\n##### CourseController Parameter openYn : " + id + "\n\n");
        Optional<Course> opt = courseRepository.findById(Long.parseLong(id));

        try {
            if (opt.isPresent()) {
                course = opt.get();

                if (param.get("openYn") != null) {
                    course.setOpenYn(Boolean.parseBoolean(param.get("openYn")));

                    course = courseRepository.save(course);
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 도 못받는다는 것을 확인:

```
# 강의 (course) 서비스를 잠시 내려놓음
cd ./pay/kubernetes
kubectl delete -f serivce.yaml

# 강좌 오픈 시도 -> 실패
http PATCH http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules/4 openYn=true studentCount=1

HTTP/1.1 500 Internal Server Error
Content-Type: application/json;charset=UTF-8
Date: Wed, 09 Jun 2021 04:53:11 GMT
transfer-encoding: chunked

{
    "error": "Internal Server Error",
    "message": "Failed during Course Open; nested exception is javax.persistence.RollbackException: Failed during Course Open",
    "path": "/courseSchedules/4",
    "status": 500,
    "timestamp": "2021-06-09T04:53:11.678+0000"
}

# 강의 (course) 서비스 재기동
kubectl apply -f serivce.yaml

# 강좌 오픈 시도 -> 성공
http PATCH http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules/4 openYn=true studentCount=1

HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Wed, 09 Jun 2021 05:05:19 GMT
transfer-encoding: chunked

{
    "_links": {
        "courseSchedule": {
            "href": "http://schedule:8080/courseSchedules/4"
        },
        "self": {
            "href": "http://schedule:8080/courseSchedules/4"
        }
    },
    "courseId": 2,
    "courseName": "korean",
    "openYn": true,
    "preOpenYn": false,
    "studentCount": 1,
    "teacher": "Hong-Gil-dong"
}
```

## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

payment(결제) 후 delivery(배송) 서비스로 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 delivery(배송)서비스 시스템 문제로 인해 강의 관리가 블로킹 되지 않아도록 처리한다.
 
- 이를 위하여 결제 승인/취소 시 자체 DB에 직접 기록을 남긴 후에 곧바로 결제 승인/취소 내용을 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package team;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String applyId;
    private String payMethod;
    private String payAccount;
    private String payStaus;
    private String addr;
    private String telephoneInfo;
    private String studentName;

    @PostPersist
    public void onPostPersist() {
        PaymentAppoved paymentAppoved = new PaymentAppoved();
        BeanUtils.copyProperties(this, paymentAppoved);
        paymentAppoved.setPayStaus("PaymentAprroved");
        paymentAppoved.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        PaymentCanceled paymentCanceled = new PaymentCanceled();
        BeanUtils.copyProperties(this, paymentCanceled);
        paymentCanceled.publishAfterCommit();
    }
```

- 강의 등록/수정 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentAppoved_DeliveryStart(@Payload PaymentAppoved paymentAppoved){

        if(!paymentAppoved.validate()) return;

        System.out.println("\n\n##### listener DeliveryStart : " + paymentAppoved.toJson() + "\n\n");

        Delivery delivery = new Delivery();

        delivery.setAddr(paymentAppoved.getAddr());
        delivery.setApplyId(paymentAppoved.getApplyId());
        delivery.setName(paymentAppoved.getStudentName());
        delivery.setTelelephoneInfo(paymentAppoved.getTelephoneInfo());
        delivery.setDeliveryStatus("DeliveryStart");

        deliveryRepository.save(delivery);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_DeliveryCancel(@Payload PaymentCanceled paymentCanceled){

        System.out.println("\n\n##### listener DeliveryCancel : " + paymentCanceled.toJson() + "\n\n");
        
        if(paymentCanceled.isMe()){
            List<Delivery> deliveryList = deliveryRepository.findByApplyId(paymentCanceled.getApplyId());
            
            if ((deliveryList != null) && !deliveryList.isEmpty()){
                deliveryRepository.deleteAll(deliveryList);
            }
        }
    }
```
- 실제 구현을 하자면, 수강생은 수강 신청/신청취소를 한 경우, 수강신청상태,결제상태, 배송상태 정보를 Mypage Aggregate 내에서 조회 가능
  
```
    @StreamListener(KafkaProcessor.INPUT)
    public void whenClassRegisted_then_CREATE_1 (@Payload ClassRegisted classRegisted) {
        try {

            System.out.println("\n\n##### listener classRegisted : " + classRegisted.toJson() + "\n\n");

            if (!classRegisted.validate()) return;

            // view 객체 생성
            Mypage mypage = new Mypage();
            // view 객체에 이벤트의 Value 를 set 함
            mypage.setApplyId(String.valueOf(classRegisted.getId()));
            mypage.setCourseId(String.valueOf(classRegisted.getCourseId()));
            mypage.setApplyStaus("ApplyFinish");
            mypage.setPayMethod(classRegisted.getPayMethod());
            mypage.setPayAccount(classRegisted.getPayAccount());
            mypage.setStudentName(classRegisted.getStudentName());
            mypage.setAddr(classRegisted.getAddr());
            mypage.setTelephoneInfo(classRegisted.getTelephoneInfo());
            mypage.setPayStatus("PayFinish");
            //mypage.setDeliveryStatus("D");
            // view 레파지 토리에 save
            mypageRepository.save(mypage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentAppoved_then_UPDATE_1(@Payload PaymentAppoved paymentAppoved) {
        try {
            if (!paymentAppoved.validate()) return;
                // view 객체 조회
                System.out.println("\n\n##### listener paymentAppoved : " + paymentAppoved.toJson() + "\n\n");

                List<Mypage> mypageList = mypageRepository.findByApplyId(paymentAppoved.getApplyId());
                for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setPayStatus("PayFinish");
                    mypage.setApplyStaus("ApplyFinish");
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_UPDATE_2(@Payload DeliveryStarted deliveryStarted) {
        try {
            if (!deliveryStarted.validate()) return;
                // view 객체 조회
                System.out.println("\n\n##### listener deliveryStarted : " + deliveryStarted.toJson() + "\n\n");

                List<Mypage> mypageList = mypageRepository.findByApplyId(deliveryStarted.getApplyId());
                for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함

                    mypage.setDeliveryStatus("DeliveryFinish");
                // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
```

- 강좌 관리 시스템은 강사 스케쥴 관리 시스템과 완전히 분리되어 있어, 이벤트 수신에 따라 처리되기 때문에, 유지보수로 인해 잠시 내려간 상태라도 강좌 관리 시스템 사용하는데 문제 없음

```
# 강사 스케쥴 관리 (schedule) 를 잠시 내려놓음 
cd ./schedule/kubernetes
kubectl delete -f deployment.yaml

# 강의 등록 
http POST  http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses name=english teacher=John-Doe fee=10000 textBook=eng_book openYn=false

HTTP/1.1 201 Created
Content-Type: application/json;charset=UTF-8
Date: Wed, 09 Jun 2021 06:45:30 GMT
Location: http://course:8080/courses/3
transfer-encoding: chunked

{
    "_links": {
        "course": {
            "href": "http://course:8080/courses/3"
        },
        "self": {
            "href": "http://course:8080/courses/3"
        }
    },
    "fee": 10000,
    "name": "english",
    "openYn": false,
    "teacher": "John-Doe",
    "textBook": "eng_book"
}

# 수강 신청 (성공) 
http POST http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes courseId=3 fee=10000 student=Ha-Jeong-Cheol textBook=eng_book

HTTP/1.1 201 Created
Content-Type: application/json;charset=UTF-8
Date: Wed, 09 Jun 2021 06:52:56 GMT
Location: http://class:8080/classes/9
transfer-encoding: chunked

{
    "_links": {
        "class": {
            "href": "http://class:8080/classes/9"
        },
        "self": {
            "href": "http://class:8080/classes/9"
        }
    },
    "courseId": 3,
    "fee": 10000,
    "student": "Ha-Jeong-Cheol",
    "textBook": "eng_book"
}

# 강좌 확인 및 수강 신청 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courses  # 강좌 등록 정보 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/classes  # 수강 신청 정보 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/mypages  # 강사용 Mypage에 강좌 등록 확인 하지만 강좌가 Open 되지는 않음 ("openYn": false)

...
            {
                "_links": {
                    "mypage": {
                        "href": "http://mypage:8080/mypages/5"
                    },
                    "self": {
                        "href": "http://mypage:8080/mypages/5"
                    }
                },
                "courseId": 3,
                "courseName": "english",
                "fee": 10000,
                "openYn": false,
                "studentCount": null,
                "teacher": "John-Doe",
                "textBook": "eng_book"
            }
...

# 강사 스케쥴 관리 서비스 (schedule) 기동
kubectl apply -f deployment.yml

# 강사 스케쥴 관리 Update 확인 및 강사 Mypage에서 상태값 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/courseSchedules # 정상 Open유무 및 수강생 수 변경 확인
http GET http://ad45ebba654ca4d4993d71580ed82c7f-474668662.eu-central-1.elb.amazonaws.com:8080/mypage  # 강사용 Mypage에 강좌 등록 확인 그리고 강좌가 Open 됨 ("openYn": true)
...
            {
                "_links": {
                    "mypage": {
                        "href": "http://mypage:8080/mypages/5"
                    },
                    "self": {
                        "href": "http://mypage:8080/mypages/5"
                    }
                },
                "courseId": 3,
                "courseName": "english",
                "fee": 10000,
                "openYn": true,
                "studentCount": 1,
                "teacher": "John-Doe",
                "textBook": "eng_book"
            }
...            
```


# 운영

## CI/CD 설정

### codebuild 사용

* codebuild를 사용하여 pipeline 생성 및 배포
![image](https://user-images.githubusercontent.com/80744192/121446820-5ff16180-c9cf-11eb-835a-82fc8e46c8a1.png)

- schedule 서비스 배포 진행 단계
![image](https://user-images.githubusercontent.com/80744192/121447072-e8700200-c9cf-11eb-9054-6e480d2c1471.png)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 수강신청 시 수강 스케쥴관리에 수강/수강 취소에 따른 강좌 Open/Close 유무 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 1000 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml

feign:
  hystrix:
    enabled: true
hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 1000
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 50명
- 60초 동안 실시

```
urls.txt 작성
http://gateway:8080/courseSchedules/1 PATCH {"openYn": true, "studentCount": 1}
http://gateway:8080/courseSchedules/1 PATCH {"openYn": false, "studentCount": 0}

root@siege:/# siege -c50 -t60S -r10 -v --content-type "application/json" -f urls.txt
[error] CONFIG conflict: selected time and repetition based testing
defaulting to time-based testing: 60 seconds
** SIEGE 4.0.4
** Preparing 50 concurrent users for battle.
The server is now under siege...

HTTP/1.1 200     6.09 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.49 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.28 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.58 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.61 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.50 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.71 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    12.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    21.51 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.92 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     0.63 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.62 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.78 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    23.82 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.71 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.90 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     1.89 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     1.21 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.81 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     0.58 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     1.56 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     1.51 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1

C/B 발생

HTTP/1.1 500     1.01 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.01 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.04 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     7.07 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     7.11 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.02 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.03 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.04 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.01 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.01 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.04 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.00 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.00 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.00 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.04 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     7.07 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     3.04 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.02 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     1.03 secs:     208 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500    30.01 secs:     179 bytes ==> PATCH http://gateway:8080/courseSchedules/1

C/B 해제됨

HTTP/1.1 200    16.25 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     2.22 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.99 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.53 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     8.53 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.62 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.72 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.72 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     2.53 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.72 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.73 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.72 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.75 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    16.92 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    17.72 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    17.84 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    17.94 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    17.95 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.01 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.02 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.05 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.04 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.05 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200    18.05 secs:     328 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.10 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.22 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 500     4.10 secs:     239 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.70 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.09 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.00 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     4.68 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.21 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     4.69 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.49 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.09 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.30 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.08 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     9.66 secs:     327 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.09 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.91 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.91 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.93 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.01 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.30 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.00 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.09 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.49 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     6.28 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.58 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.59 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.61 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.50 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.71 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
HTTP/1.1 200     5.40 secs:     326 bytes ==> PATCH http://gateway:8080/courseSchedules/1
...

Transactions:                   1023 hits
Availability:                  49.28 %
Elapsed time:                   8.55 secs
Data transferred:               0.56 MB
Response time:                  0.40 secs
Transaction rate:             119.65 trans/sec
Throughput:                     0.07 MB/sec
Concurrency:                   48.30
Successful transactions:        1023
Failed transactions:            1053
Longest transaction:            0.84
Shortest transaction:           0.01

```
- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 49% 가 성공하였고, 51%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

## 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 강좌 관리 및 강자 스케쥴 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 30프로를 넘어서면 replica 를 10개까지 늘려준다
```
kubectl autoscale deploy schedule --min=1 --max=10 --cpu-percent=30
kubectl autoscale deploy course --min=1 --max=10 --cpu-percent=30
```
- CB 에서 했던 방식대로 워크로드를 50초 동안 걸어준다. 
```
siege -c50 -t60S -r10 -v --content-type "application/json" -f urls.txt
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
watch kubectl get pod,hpa
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
NAME                                           REFERENCE             TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/course     Deployment/course     8%/30%    1         10        1          41m
horizontalpodautoscaler.autoscaling/schedule   Deployment/schedule   46%/30%   1         10        10         41m

NAME                           READY   STATUS    RESTARTS   AGE
pod/alert-7f7dcbb7dd-5tsd9     2/2     Running   0          7h23m
pod/class-68674c6bf-nqx4l      1/1     Running   0          7h22m
pod/course-55f66f8d6-ppvnh     1/1     Running   0          42m
pod/gateway-845cfdd6cd-fqjzj   1/1     Running   0          7h14m
pod/mypage-577f8b5466-llfs7    1/1     Running   0          7h11m
pod/pay-7bfdcdff75-c7q2p       1/1     Running   0          7h2m
pod/schedule-98c86dbc8-2m57c   1/1     Running   0          3m22s
pod/schedule-98c86dbc8-4c9gv   1/1     Running   0          3m22s
pod/schedule-98c86dbc8-llcwb   1/1     Running   0          3m7s 
pod/schedule-98c86dbc8-mkxb6   1/1     Running   0          7m26s
pod/schedule-98c86dbc8-rf8nx   1/1     Running   0          7m26s
pod/schedule-98c86dbc8-s5l68   1/1     Running   0          3m22s
pod/schedule-98c86dbc8-vfqb9   1/1     Running   0          3m22s
pod/schedule-98c86dbc8-z74c9   1/1     Running   0          7m26s
pod/schedule-98c86dbc8-zk4dm   1/1     Running   0          3m7s 
pod/schedule-98c86dbc8-zt886   1/1     Running   2          51m
pod/siege                      1/1     Running   0          77m
:
```
- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
```
Transactions:                  27181 hits
Availability:                 100.00 %
Elapsed time:                  59.47 secs
Data transferred:               8.50 MB
Response time:                  0.11 secs
Transaction rate:             457.05 trans/sec
Throughput:                     0.14 MB/sec
Concurrency:                   49.04
Successful transactions:       27181
Failed transactions:               0
Longest transaction:            0.79
Shortest transaction:           0.00
```


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 -v --content-type "application/json" 'http://gateway:8080/courses POST {"name": "english", "teacher": "Jane-Doe", "fee": 10000, "textBook": "eng_book"}'


** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     3.43 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.28 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     0.20 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     3.44 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.18 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     0.28 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.41 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.22 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     0.21 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     0.13 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.41 secs:     251 bytes ==> POST http://gateway:8080/courses
HTTP/1.1 201     1.31 secs:     251 bytes ==> POST http://gateway:8080/courses

```

- 새버전(v1.0)으로의 배포 시작
```
kubectl apply -f kubectl apply -f deployment_v1.0.yml

```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:                    614 hits
Availability:                  35.35 %
Elapsed time:                  34.95 secs
Data transferred:               0.38 MB
Response time:                  3.87 secs
Transaction rate:              17.57 trans/sec
Throughput:                     0.01 MB/sec
Concurrency:                   68.06
Successful transactions:         614
Failed transactions:            1123
Longest transaction:           29.72
Shortest transaction:           0.00
```
- 배포 중 Availability 가 평소 100%에서 35% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```
# deployment.yaml 의 readiness probe 의 설정:

# (schedule) deployment.yaml 파일
           readinessProbe:
            httpGet:
              path: '/courseSchedules'
              port: 8080
            initialDelaySeconds: 20
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
           
# (course) deployment.yaml 파일
           readinessProbe:
            httpGet:
              path: '/courses'
              port: 8080
            initialDelaySeconds: 20
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10

/> kubectl apply -f deployment.yml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Lifting the server siege...
Transactions:                  39737 hits
Availability:                 100.00 %
Elapsed time:                 119.91 secs
Data transferred:               9.66 MB
Response time:                  0.30 secs
Transaction rate:             331.39 trans/sec
Throughput:                     0.08 MB/sec
Concurrency:                   99.71
Successful transactions:       39737
Failed transactions:               0
Longest transaction:            1.89
Shortest transaction:           0.00

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

## Self-healing (Liveness Probe)

* mysql DB에 간단 Query를 실행하여 서비스가 정상 동작하는지 확인하고 문제 발생 시 Pod 서비스를 재기동함

- "course_schedule_tablesel" 테이블에 ID가 1인 값이 존재하는 체크

```
# deployment.yaml 의 liveness probe 의 설정:
# (schedule) deployment.yaml 파일
          livenessProbe:
            httpGet:
              path: '/courseSchedules/1'
              port: 8080
            initialDelaySeconds: 180
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```
- mysql db에서 직접 id가 1인 record 삭제

```
mysql> select * from course_schedule_table;
+----+-----------+-------------+---------+---------------+---------------+
| id | course_id | course_name | open_yn | student_count | teacher       |
+----+-----------+-------------+---------+---------------+---------------+
|  1 |         1 | korean      |         |             0 | Hong-Gil-dong |
|  2 |         3 | english     |        |             1 | John-Doe      |
|  3 |         1 | korean      |         |             0 | Hong-Gil-dong |
|  4 |         2 | korean      |        |             1 | Hong-Gil-dong |
|  5 |         3 | english     |        |             1 | John-Doe      |
|  6 |         2 | mathematics |         |             0 | Jane-Doe      |
+----+-----------+-------------+---------+---------------+---------------+
6 rows in set (0.00 sec)

mysql> delete from course_schedule_table where id = 1;
Query OK, 1 row affected (0.01 sec)
```

- schedule POD RESTARTS 수 증가 발생
```
NAME                           READY   STATUS    RESTARTS   AGE
pod/schedule-f9858b878-9hlhs   0/1     Running   1          9m28s

...

# 비정상 동작 CPU 사용률 급증, Auto Scale-out 하였지만 정상 동상 실패
pod/schedule-f9858b878-9hlhs   0/1     Running             1          10m
pod/schedule-f9858b878-h6cct   0/1     ContainerCreating   0          0s
pod/schedule-f9858b878-kj7pt   0/1     ContainerCreating   0          0s
pod/schedule-f9858b878-s88dc   0/1     ContainerCreating   0          0s

NAME                                           REFERENCE             TARGETS    MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/course     Deployment/course     0%/30%     1         10        1          89m
horizontalpodautoscaler.autoscaling/schedule   Deployment/schedule   100%/30%   1         10        1          89m

...

# 점차 모든 Pod가 RESTART 하기 시작함
NAME                           READY   STATUS    RESTARTS   AGE
pod/schedule-f9858b878-9hlhs   0/1     Running   2          13m
pod/schedule-f9858b878-h6cct   0/1     Running   1          3m26s
pod/schedule-f9858b878-kj7pt   1/1     Running   0          3m26s
pod/schedule-f9858b878-s88dc   0/1     Running   1          3m26s
```
- mysql db에서 직접 id가 1인 record 등록

```
mysql> insert into course_schedule_table (id, course_id, course_name, open_yn, student_count, teacher) values (1, 1, "mathematics", false, 0, "Jane-Doe");
Query OK, 1 row affected (0.00 sec)

mysql> select * from course_schedule_table;
+----+-----------+-------------+---------+---------------+---------------+
| id | course_id | course_name | open_yn | student_count | teacher       |
+----+-----------+-------------+---------+---------------+---------------+
|  1 |         1 | mathematics |         |             0 | Jane-Doe      |
|  2 |         3 | english     |        |             1 | John-Doe      |
|  3 |         1 | korean      |         |             0 | Hong-Gil-dong |
|  4 |         2 | korean      |        |             1 | Hong-Gil-dong |
|  5 |         3 | english     |        |             1 | John-Doe      |
|  6 |         2 | mathematics |         |             0 | Jane-Doe      |
+----+-----------+-------------+---------+---------------+---------------+
6 rows in set (0.00 sec)
```
- POD 정상 동작
```
NAME                           READY   STATUS    RESTARTS   AGE
pod/schedule-f9858b878-9hlhs   1/1     Running   2          15m
pod/schedule-f9858b878-h6cct   1/1     Running   1          5m39s
```

## 개발 운영 환경 분리 및 log파일 영구 보관을 위한 EFS와 연동
* ConfigMap을 사용하여 운영과 개발 환경 분리

- kafka환경 분리
```
  운영 : kafka-1621824578.kafka.svc.cluster.local:9092
  개발 : localhost:9092
```
- configmap를 활용하여 환경 정보 등록
```
configmap yaml 파일

apiVersion: v1
kind: ConfigMap
metadata:
  name: kafka-config
data:
  KAFKA_URL: kafka-1621824578.kafka.svc.cluster.local:9092
  LOG_FILE: /tmp/logs/debug.log
```
- deploy 파일를 통해 환경 변수 주입
```
deployment yaml 파일

       - name: consumer
          image: 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/lecture-consumer:latest 
          env:
          - name: KAFKA_URL
            valueFrom:
              configMapKeyRef:
                name: kafka-config
                key: KAFKA_URL
          - name: LOG_FILE
            valueFrom:
              configMapKeyRef:
                name: kafka-config
                key: LOG_FILE
```
- 프로그램에서 사용
```
프로그램(python) 파일

from kafka import KafkaConsumer
from logging.config import dictConfig
import logging
import os

kafka_url = os.getenv('KAFKA_URL')
log_file = os.getenv('LOG_FILE')

consumer = KafkaConsumer('lecture', bootstrap_servers=[
                         kafka_url], auto_offset_reset='earliest', enable_auto_commit=True, group_id='alert')


```
- 로그 파일 저장을 위해 PersistentVolumeClaim 및 StorageClass 구성
```
root@labs--1801447399:/home/project/team/alert/kubernetes# kubectl get pvc,cm,sc
NAME                            STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
persistentvolumeclaim/aws-efs   Bound    pvc-4586fcaf-7e53-4dfb-9f48-2de397908a9a   1Mi        RWX            aws-efs        16m

NAME                     DATA   AGE
configmap/kafka-config   2      11h

NAME                                        PROVISIONER             RECLAIMPOLICY   VOLUMEBINDINGMODE      ALLOWVOLUMEEXPANSION   AGE
storageclass.storage.k8s.io/aws-efs         my-aws.com/aws-efs      Delete          Immediate              false                  19m
storageclass.storage.k8s.io/gp2 (default)   kubernetes.io/aws-ebs   Delete          WaitForFirstConsumer   false                  12h
```
- deployment yaml 파일 구성
```
  template:
    metadata:
      labels:
        app: alert
    spec:
      containers:
        - name: consumer
          image: 879772956301.dkr.ecr.eu-central-1.amazonaws.com/user23-consumer:latest
          volumeMounts:
          - mountPath: "/tmp/logs"
            name: volume
          env:
          - name: KAFKA_URL
            valueFrom:
              configMapKeyRef:
                name: kafka-config
                key: KAFKA_URL
          - name: LOG_FILE
            valueFrom:
              configMapKeyRef:
                name: kafka-config
                key: LOG_FILE
        - name: web
          image: 879772956301.dkr.ecr.eu-central-1.amazonaws.com/user23-web:latest
          ports:
            - containerPort: 8084
          readinessProbe:
            httpGet:
              path: '/alert'
              port: 8084
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/alert'
              port: 8084
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: aws-efs
```
- Container에 접속하여 mount 확인
```
root@labs--1801447399:/home/project/team/alert/kubernetes# kubectl exec -it pod/alert-6b5db4674f-7t7gl -c consumer -- /bin/bash
root@alert-6b5db4674f-7t7gl:/app# df -k
Filesystem                                                                                          1K-blocks    Used        Available Use% Mounted on
overlay                                                                                              83873772 3922208         79951564   5% /
tmpfs                                                                                                   65536       0            65536   0% /dev
tmpfs                                                                                                 1988928       0          1988928   0% /sys/fs/cgroup
fs-401bb61b.efs.eu-central-1.amazonaws.com:/aws-efs-pvc-4586fcaf-7e53-4dfb-9f48-2de397908a9a 9007199254739968       0 9007199254739968   0% /tmp/logs
/dev/nvme0n1p1                                                                                       83873772 3922208         79951564   5% /etc/hosts
shm                                                                                                     65536       0            65536   0% /dev/shm
tmpfs                                                                                                 1988928      12          1988916   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs                                                                                                 1988928       0          1988928   0% /proc/acpi
tmpfs                                                                                                 1988928       0          1988928   0% /sys/firmware
root@alert-6b5db4674f-7t7gl:/app# cd /tmp/logs
root@alert-6b5db4674f-7t7gl:/tmp/logs# cat *
[2021-06-09 14:13:46,894] KAFKA URL : my-kafka.kafka.svc.cluster.local:9092
[2021-06-09 14:13:46,894] LOG_FILE : /tmp/logs/debug.log
root@alert-6b5db4674f-7t7gl:/tmp/logs# 
```

## 모니터링
* istio 설치, Kiali 구성, Jaeger 구성, Prometheus 및 Grafana 구성
* istio 설치
```
root@labs--1801447399:/home/project/team/istio-1.7.1# kubectl get all -n istio-system
NAME                                        READY   STATUS    RESTARTS   AGE
pod/istio-egressgateway-74f9769788-2cgnt    1/1     Running   0          3m55s
pod/istio-ingressgateway-74645cb9df-v9dlg   1/1     Running   0          3m55s
pod/istiod-756fdd548-s5vzl                  1/1     Running   0          4m8s
pod/kiali-79b754f6d-j4n9m                   1/1     Running   0          54s

NAME                           TYPE           CLUSTER-IP       EXTERNAL-IP                                                                 PORT(S)                                                                      AGE
service/istio-egressgateway    ClusterIP      10.100.229.225   <none>                                                                      80/TCP,443/TCP,15443/TCP                                                     3m54s
service/istio-ingressgateway   LoadBalancer   10.100.227.165   ab6b31616c6a74e14b50ffdcb19f59e8-599823368.eu-central-1.elb.amazonaws.com   15021:32125/TCP,80:30963/TCP,443:30449/TCP,31400:30620/TCP,15443:31113/TCP   3m54s
service/istiod                 ClusterIP      10.100.83.38     <none>                                                                      15010/TCP,15012/TCP,443/TCP,15014/TCP,853/TCP                                4m8s
service/kiali                  ClusterIP      10.100.239.120   <none>                                                                      20001/TCP,9090/TCP                                                           56s

NAME                                   READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/istio-egressgateway    1/1     1            1           3m56s
deployment.apps/istio-ingressgateway   1/1     1            1           3m56s
deployment.apps/istiod                 1/1     1            1           4m9s
deployment.apps/kiali                  1/1     1            1           55s

NAME                                              DESIRED   CURRENT   READY   AGE
replicaset.apps/istio-egressgateway-74f9769788    1         1         1       3m56s
replicaset.apps/istio-ingressgateway-74645cb9df   1         1         1       3m56s
replicaset.apps/istiod-756fdd548                  1         1         1       4m9s
replicaset.apps/kiali-79b754f6d                   1         1         1       55s
```

- default namespace istio 활성화
```
kubectl label namespace default istio-injection=enabled
```
- pod 재기동 (container 수 2 이상 확인)
```
root@labs--1801447399:/home/project/team/istio-1.7.1# kubectl get all
NAME                                   READY   STATUS    RESTARTS   AGE
pod/alert-6b5db4674f-lqbqz             3/3     Running   1          3m3s
pod/class-68674c6bf-hl4rn              2/2     Running   0          3m3s
pod/course-55f66f8d6-659kq             2/2     Running   0          3m2s
pod/efs-provisioner-6b4d7c8584-g5dkb   1/1     Running   0          41m
pod/gateway-845cfdd6cd-shlsv           2/2     Running   0          3m2s
pod/mypage-577f8b5466-48k6m            2/2     Running   0          3m2s
pod/pay-7bfdcdff75-7mjgg               2/2     Running   0          3m2s
pod/schedule-f9858b878-mb7rl           2/2     Running   0          3m1s
pod/siege                              1/1     Running   0          5h55m
pod/ubuntu                             1/1     Running   0          3h58m
```
* Kiali 설치
- service type : ClusterIP -> LoadBalancer 로 수정
```
  selector:
    app.kubernetes.io/instance: kiali-server
    app.kubernetes.io/name: kiali
  sessionAffinity: None
  type: LoadBalancer
status:
  loadBalancer: {}
```
- Kiali URL : http://ab836d7c5b3dc470a812f23ee70749ca-356850465.eu-central-1.elb.amazonaws.com:20001/
![image](https://user-images.githubusercontent.com/80744192/121379666-36a8e500-c97f-11eb-9809-4723ac0e2a93.png)


* Jaeger 설치
- service type : ClusterIP -> LoadBalancer 로 수정
- Jaeger URL : http://aa4f29e4750e6457e8ea0a652d092937-1251552130.eu-central-1.elb.amazonaws.com/
![image](https://user-images.githubusercontent.com/80744192/121380205-a5863e00-c97f-11eb-813c-2e3544ce12d4.png)


* Prometheus 및 Grafana 설치
- service type : ClusterIP -> LoadBalancer 로 수정
- Grafana URL : http://http://a17ce955b36c643dba43634c3958f665-1939868886.ap-northeast-2.elb.amazonaws.com:3000/
![image](https://user-images.githubusercontent.com/80744192/121453266-d85e1f80-c9db-11eb-88ec-75e33bbae74b.png)

