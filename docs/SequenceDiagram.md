### 0. 초기 데이터
- 사용자, 쿠폰, 상품은 초기 데이터로 세팅을 합니다.

### 1. 유저 포인트 조회

- 기능 : 사용자 포인트 조회를 합니다.
- 검증 : 사용자의 userId를 검증합니다.

```mermaid
sequenceDiagram
    actor Client
    participant Usr as 사용자
    participant Point as 포인트
    
    Client->>Usr: 사용자 조회
    
    alt사용자가 존재하는 경우
        Usr->>Point: 포인트 조회
        Point-->>Usr: 포인트 정보 반환
        Usr-->>Client: 유저 포인트 Response
    else 사용자가 존재하지 않는 경우
        Usr-->>Client: 실패 응답 반환
    end
```



### 2. 포인트 충전

- 기능 : 사용자 포인트를 충전합니다.
- 검증
  - 사용자의 userId를 검증합니다.
  - 충전 포인트 음수, 0을 검증합니다.

```mermaid
sequenceDiagram
    actor Client
    participant Usr as 사용자
    
    Client->>Usr: 포인트 충전 Request
    
    alt 사용자 존재 확인
        Usr->>Usr: 사용자 validation
        
        alt 사용자가 존재하지 않는 경우
            Usr-->>Client: 사용자 없음 Fail Response
        else 사용자가 존재하는 경우
            Usr->>Usr: 포인트 충전 검증
            
            alt 충전 포인트 음수 또는 0보다 작으면 실패
                Usr-->>Client: 충전 Fail Response
            else 충전 포인트가 정상인 경우
                Usr->>Usr: 포인트 충전 처리
                Usr-->>Client: 충전 Success Response
            end
        end
    end
```

### 3. 상품 조회
- 기능 : 상품의 정보와 재고를 조회합니다.
  - 카테고리에 해당하는 상품을 조회합니다.
- 검증
    - 필터 값이 없으면 전체 상품을 조회합니다.
    - 필터가 있으면 해당 카테고리의 상품을 조회합니다.

```mermaid
sequenceDiagram
    actor Users
    participant 상품
    participant Stock as 재고
    
    Users->>상품: 상품 목록 조회 Request
    
    alt 카테고리 조회 값이 없는 경우
        Note over 상품: 전체 상품 목록 조회
        상품->>Stock: 상품별 재고 정보 요청
        Stock-->>상품: 상품별 재고 정보 응답
        Note over 상품: 상품 정보와 재고 정보 결합
        상품-->>Users: 재고 정보가 포함된 전체 상품 목록 Response
    else 유효한 조건인 경우
        Note over 상품: 조건(카테고리)에 해당하는 상품 조회 (List)
        상품->>Stock: 조회된 상품별 재고 정보 요청
        Stock-->>상품: 조회된 상품별 재고 정보 응답
        Note over 상품: 상품 정보와 재고 정보 결합
        상품-->>Users: 재고 정보가 포함된 상품 조회 목록 Response
    end
```

### 4. 사용자 쿠폰 조회
- 기능 : 사용자의 쿠폰을 조회합니다.
- 검증
    - 사용자를 검증합니다.
    - 보유 쿠폰 없으면 쿠폰 목록이 없다고 응답합니다.
    - 보유 쿠폰이 있으면 쿠폰 목록을 응답합니다.
```mermaid
sequenceDiagram
    actor Client
    participant Usr as 사용자
    participant Coupon as 쿠폰
    
    Client->>Usr: 사용자 쿠폰 조회 요청
    
    Usr->>Usr: 사용자 존재 확인
    
    alt 사용자가 존재하지 않는 경우
        Usr-->>Client: 사용자 없음 응답
    else 사용자가 존재하는 경우
        Usr-->>Coupon: 사용자 쿠폰 목록 요청
        
        Coupon->>Coupon: 사용자 보유 쿠폰 검색
        
        alt 보유 쿠폰이 없는 경우
            Coupon-->>Usr: 보유 쿠폰 없음 응답
            Usr-->>Client: 쿠폰 목록 없음 응답
        else 보유 쿠폰이 있는 경우
            Coupon-->>Usr: 사용자 쿠폰 목록 반환
            Usr-->>Client: 쿠폰 목록 응답
        end
    end
```

### 5. 쿠폰 발급
- 기능 : 사용자가 쿠폰을 발급합니다.
- 검증
    - 사용자를 검증합니다.
    - 쿠폰 ID가 없으면 실패합니다.
    - 쿠폰 만료 시간이 30초 이하인 경우 발급이 실패합니다.
    - 이미 발급 받은 경우 실패합니다.
    - 쿠폰 발급 수량이 0인 경우 실패합니다.
- 성공
  - 발급이 성공하면 쿠폰의 수량을 차감합니다.
  - 사용자와 쿠폰 매ㅊ핑 정보를 저장합니다.
```mermaid
sequenceDiagram
    actor Client
    participant Usr as 사용자
    participant Coupon
    
    Client->>Coupon: 쿠폰 발급 요청
    Coupon->>Usr: 사용자 존재 확인
    
    alt 사용자가 없는 경우
        Usr-->>Coupon: 사용자 없음 응답
        Coupon->>Client: 쿠폰 발급 실패 (존재하지 않는 사용자)
    else 사용자가 존재하는 경우
        Usr-->>Coupon: 사용자 존재 확인 응답
        Coupon->>Coupon: 쿠폰 정보 확인
        
        alt 쿠폰ID가 없는 경우
            Coupon->>Client: 쿠폰 발급 실패 (없는 쿠폰)
        else 쿠폰 만료 시간이 30초 이하인 경우
            Coupon->>Client: 쿠폰 발급 실패 (유효 시간 부족)
        else 쿠폰이 유효한 경우
            Coupon->>Coupon: 사용자 발급 이력 확인
            
            alt 이미 발급 받은 경우
                Coupon->>Client: 쿠폰 중복 발급 오류
            else 발급 가능 수량이 0인 경우
                Coupon->>Client: 쿠폰 발급 실패 (수량 부족)
            else 발급 가능한 경우
                Coupon->>Coupon: 발급 가능 수량 차감
                Coupon->>Usr: 사용자-쿠폰 매핑 정보 저장
                Coupon->>Client: 쿠폰 발급 성공
            end
        end
    end
```



### 6. 주문
- 기능 : 사용자가 N개의 상품을 주문합니다. ( 상태 : 결제대기 )
- 검증
    - 사용자를 검증합니다.
    - 상품 ID를 검증합니다.
    - 재고를 검증합니다.
- 성공
    - N개의 상품을 주문목록에 생성됩니다.
    - 주문은 10분의 ExpireTime 가집니다.
    - 10분이 지나면 `결제실패` 상태가 바뀌면서 재고가 복원됩니다.
```mermaid
sequenceDiagram
    actor Client
    participant UserAuth as User
    participant Order
    participant Product
    participant Stock
    
    Client->>Order: 주문 요청
    
    Order->>UserAuth: 사용자 유효성 검증 요청
    
    alt 사용자가 유효하지 않은 경우
        UserAuth-->>Order: 사용자 유효성 검증 실패
        Order-->>Client: 주문 실패 응답 (인증 오류)
    else 사용자가 유효한 경우
        UserAuth-->>Order: 사용자 유효성 검증 성공
        
        Order->>Product: 상품 ID 검증 요청
        
        alt 상품 ID가 유효하지 않은 경우
            Product-->>Order: 상품 ID 검증 실패 (존재하지 않는 상품)
            Order-->>Client: 주문 실패 응답 (상품 ID 오류)
        else 상품 ID가 유효한 경우
            Product-->>Order: 상품 ID 검증 성공
            
            Order->>Product: 상품 정보 요청(상품ID)
            Product-->>Order: 상품 정보 반환
            
            Order->>Stock: 재고 확인 요청(상품ID, 수량)
            
            alt 재고 부족
                Stock-->>Order: 재고 부족 응답
                Order-->>Client: 주문 실패 응답 (재고 부족)
            else 재고 충분
                Stock-->>Order: 재고 충분 응답
                
                Order->>Order: 주문 생성
                Order->>Stock: 재고 차감 요청
                Stock-->>Stock: 재고 차감 처리
                Order-->>Client: 주문 성공 응답
                
                Note over Order: 주문 만료 ExpireTime (10분)
                Order->>Order: 10분 후 만료 확인
                
                alt 주문 만료됨
                    Note over Order: 주문 만료 처리
                    Order->>Stock: 재고 복원 요청
                    Stock-->>Stock: 재고 복원 처리
                end
            end
        end
    end
```

### 7. 결제

- 기능 : 사용자가 결제대기 상태의 주문을 결제하는 기능입니다.

- 검증
  - 사용자 검증: 유효한 사용자인지 확인합니다.
  - 주문 정보 확인: 주문이 존재하는지 확인합니다.
  - 주문 상태 확인: 주문 상태가 '결제대기'인지 확인합니다.
  - 쿠폰 검증: 쿠폰 사용 시 쿠폰의 유효성을 확인합니다.

- 프로세스

### 프로세스

1. 사용자가 결제 요청을 보냅니다.
2. 시스템은 주문 정보 존재 여부를 확인합니다.
3. 시스템은 사용자 정보를 검증합니다.
4. 시스템은 주문 상태가 '결제대기'인지 확인합니다.

5. 쿠폰 사용 여부에 따른 처리:
    - **쿠폰 사용 시**: 쿠폰 유효성 확인 후 할인된 금액을 계산합니다.
    - **쿠폰 미사용 시**: 정상 금액을 계산합니다.

6. 결제 시스템에 결제를 요청합니다.
7. 잔액이 충분한 경우 다음 단계를 진행합니다:
    - 사용자의 잔액을 차감합니다.
    - 쿠폰 사용 시 쿠폰 상태를 변경합니다.
    - 주문 상태를 '결제완료'로 변경합니다.
    - 결제 성공 메시지를 사용자에게 전송합니다.
    - 주문 데이터를 데이터센터로 전송합니다.

```mermaid
sequenceDiagram
    actor Client
    participant Order
    participant UserAuth as User
    participant Coupon
    participant Payment
    participant MockDataCenter
    
    Client->>Order: 결제 요청
    
    alt 주문 정보 없음
        Order-->>Client: 오류 반환 (주문 정보 없음)
    else 주문 정보 존재
        Order->>UserAuth: 사용자 정보 검증
        
        alt 사용자 정보 유효하지 않음
            UserAuth-->>Order: 사용자 검증 실패
            Order-->>Client: 결제 실패 (사용자 인증 오류)
        else 사용자 정보 유효함
            UserAuth-->>Order: 사용자 검증 성공
            
            Order->>Order: 주문 상태 확인
            
            alt 주문 상태가 결제대기가 아님
                Order-->>Client: 결제 실패 (잘못된 주문 상태)
            else 주문 상태가 결제대기임
                Order->>Order: 주문 정보 검증
                
                alt 쿠폰 사용 요청
                    Order->>Coupon: 쿠폰 유효성 확인
                    alt 쿠폰 유효하지 않음
                        Coupon-->>Order: 쿠폰 유효하지 않음
                        Order-->>Client: 결제 실패 (유효하지 않은 쿠폰)
                    else 쿠폰 유효함
                        Coupon-->>Order: 쿠폰 유효함
                        Order->>Order: 할인 적용 최종 금액 계산
                    end
                else 쿠폰 미사용
                    Order->>Order: 정상 금액 계산
                end
                
                Order->>Payment: 결제 요청
                Payment->>Client: 잔액 확인
                
                alt 잔액 부족
                    Client-->>Payment: 잔액 부족
                    Payment-->>Order: 결제 실패 (잔액 부족)
                    Order-->>Client: 결제 실패 메시지
                else 잔액 충분
                    Client-->>Payment: 잔액 충분
                    Payment->>Client: 잔액 차감
                    
                    alt 쿠폰 사용 요청
                        Payment->>Coupon: 쿠폰 사용 처리
                        Coupon-->>Coupon: 쿠폰 사용 상태 변경
                    end
                    
                    Payment-->>Order: 결제 성공
                    Order->>Order: 주문 상태 "결제완료"로 변경
                    Order-->>Client: 결제 성공 메시지
                    Order->>MockDataCenter: 주문 데이터 전송
                    MockDataCenter-->>Order: 데이터 수신 확인
                end
            end
        end
    end
```

### 8. 인기 상품 조회

```mermaid
sequenceDiagram
    actor Client
    participant HotProduct
    
    Client->>HotProduct: 상위 판매 상품 조회 요청
    HotProduct-->>Client: 상위 상품 목록 반환
```