#### 1. 유저 포인트 조회
- Request : user_id
```mermaid

sequenceDiagram
    title: 유저 포인트 조회
actor Clinet

Clinet->>User:포인트 조회 Request
alt 
    User->>User: 포인트 조회
    User-->>Clinet: 유저 포인트 Response
end
```


### 2. 포인트 충전
- Request : user_id, amount
```mermaid
sequenceDiagram
    actor Client
    participant User
    
    Client->>User: 포인트 충전 Request
    User->>User: 포인트 충전
    
    alt 충전 포인트 음수 또는 0보다 작으면 실패
        User-->>Client: 충전 Fail Response
    else
        User-->>Client: 충전 Success Response
    end
```

### 3. 상품 조회

```mermaid
sequenceDiagram
    actor Users
    participant Product
    
    Users->>Product: 상품 목록 조회 Request
    
    alt 유효한 조건인 경우
        Note over Product: 조건(카테고리, 옵션)에 해당 상품 조회 (List)
        Product-->>Users: 상품 조회 목록 Response
    else 올바르지 않은 값이 들어온 경우
        Product-->>Users: 유효성 검증 오류 Response
    end
```

### 4. 쿠폰 발급

```mermaid
sequenceDiagram
    actor Users
    participant Coupon
    
    Users->>Coupon: 쿠폰 발급 요청
    Coupon->>Coupon: 사용자 발급 이력 확인
    alt 이미 발급 받은 경우
        Coupon->>Users: 쿠폰 중복 발급 오류
    else 발급 가능 수량이 0인 경우
        Coupon->>Users: 쿠폰 발급 실패 (수량 부족)
    else 발급 가능한 경우
        Note right of Coupon: 발급 가능 수량 차감
        Note right of Coupon: 사용자-쿠폰 매핑 정보 저장
        Coupon->>Users: 쿠폰 발급 성공
    end
```

### 5. 쿠폰 조회

```mermaid
sequenceDiagram
    title: 5. 보유 쿠폰 목록 조회
    actor Users
    participant User
    
    Users->>User: 보유 쿠폰 목록 조회 요청(user_id)
    Note right of User: 사용자가 보유한 쿠폰 목록 조회
    User->>Users: 보유 쿠폰 목록 반환
```


### 6. 주문 

```mermaid
sequenceDiagram
    
    actor User
    participant Order
    participant Product
    participant Stock
    
    User->>Order: 주문 요청
    
    Order->>Product: 상품 정보 요청(상품ID)
    Product-->>Order: 상품 정보 반환
        
    Order->>Stock: 재고 확인 요청(상품ID, 수량)
    
    alt 재고 부족
        Stock-->>Order: 재고 부족 응답
        Order-->>User: 주문 실패 응답 (재고 부족)
    else 재고 충분
        Stock-->>Order: 재고 충분 응답
        
        Order->>Order: 주문 생성
        Order->>Stock: 재고 차감 요청
        Stock-->>Stock: 재고 차감 처리
        
        Order-->>User: 주문 성공 응답 (총액 포함)
        
        Note over Order: 주문 만료 ExpireTime (10분)
        
        Order->>Order: 10분 후 만료 확인
        
        alt 주문 만료됨
            Note over Order: 주문 만료 처리
            Order->>Stock: 재고 복원 요청
            Stock-->>Stock: 재고 복원 처리
        end
    end
```

### 7. 결제

```mermaid
sequenceDiagram
    
    actor User
    participant Order
    participant Coupon
    participant Payment
    participant MockDataCenter
    
    User->>Order: 결제 요청
    
    alt 쿠폰 사용 요청
        Order->>Coupon: 쿠폰 유효성 확인
        alt 쿠폰 유효하지 않음
            Coupon-->>Order: 쿠폰 유효하지 않음
            Order-->>User: 결제 실패 (유효하지 않은 쿠폰)
        else 쿠폰 유효함
            Coupon-->>Order: 쿠폰 유효함
            Order->>Order: 할인 적용 최종 금액 계산
        end
    else 쿠폰 미사용
        Order->>Order: 정상 금액 계산
    end
    
    Order->>Payment: 결제 요청
    Payment->>User: 잔액 확인
    
    alt 잔액 부족
        User-->>Payment: 잔액 부족
        Payment-->>Order: 결제 실패 (잔액 부족)
        Order-->>User: 결제 실패 메시지
    else 잔액 충분
        User-->>Payment: 잔액 충분
        Payment->>User: 잔액 차감
        
        alt 쿠폰 사용 요청
            Payment->>Coupon: 쿠폰 사용 처리
            Coupon-->>Coupon: 쿠폰 사용 상태 변경
        end
        
        Payment-->>Order: 결제 성공
        Order-->>User: 결제 성공 메시지
        Order->>MockDataCenter: 주문 데이터 전송
        MockDataCenter-->>Order: 데이터 수신 확인

    end
```

### 8. 인기 상품 조회
```mermaid
sequenceDiagram
    actor Users
    participant HotProduct
    
    Users->>HotProduct: 상위 판매 상품 조회 요청
        Note right of DailyOrderStats: 상위 상품 목록 조회
-->>Users: 상위 상품 목록 반환
```