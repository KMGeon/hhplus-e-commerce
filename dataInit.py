import random
from datetime import datetime, timedelta
import mysql.connector
from tqdm import tqdm  # 진행률 표시를 위한 라이브러리


def generate_and_insert_dummy_data(num_records):
    try:
        # MySQL 연결 설정
        conn = mysql.connector.connect(
            host='localhost',
            user='root',
            password='root',
            database='hhplus',
            port=3306
        )
        cursor = conn.cursor()

        # 상품 정보 목록 (DB에서 가져온 데이터)
        products = [
            {'product_id': 1, 'sku_id': 'A-0001-0001', 'unit_price': 2000, 'product_name': 'Apple-1',
             'category': 'APPLE'},
            {'product_id': 2, 'sku_id': 'A-0001-0002', 'unit_price': 2100, 'product_name': 'Apple-2',
             'category': 'APPLE'},
            {'product_id': 3, 'sku_id': 'A-0001-0003', 'unit_price': 2200, 'product_name': 'Apple-3',
             'category': 'APPLE'},
            {'product_id': 4, 'sku_id': 'A-0001-0004', 'unit_price': 2300, 'product_name': 'Apple-4',
             'category': 'APPLE'},
            {'product_id': 5, 'sku_id': 'A-0001-0005', 'unit_price': 2400, 'product_name': 'Apple-5',
             'category': 'APPLE'},
            {'product_id': 6, 'sku_id': 'A-0001-0006', 'unit_price': 2500, 'product_name': 'Apple-6',
             'category': 'APPLE'},
            {'product_id': 7, 'sku_id': 'A-0001-0007', 'unit_price': 2600, 'product_name': 'Apple-7',
             'category': 'APPLE'},
            {'product_id': 8, 'sku_id': 'A-0001-0008', 'unit_price': 2700, 'product_name': 'Apple-8',
             'category': 'APPLE'},
            {'product_id': 9, 'sku_id': 'A-0001-0009', 'unit_price': 2800, 'product_name': 'Apple-9',
             'category': 'APPLE'},
            {'product_id': 10, 'sku_id': 'A-0001-0010', 'unit_price': 2900, 'product_name': 'Apple-10',
             'category': 'APPLE'},
            {'product_id': 11, 'sku_id': 'D-0001-0001', 'unit_price': 2000, 'product_name': 'Dell-1',
             'category': 'DELL'},
            {'product_id': 12, 'sku_id': 'D-0001-0002', 'unit_price': 2100, 'product_name': 'Dell-2',
             'category': 'DELL'},
            {'product_id': 13, 'sku_id': 'D-0001-0003', 'unit_price': 2200, 'product_name': 'Dell-3',
             'category': 'DELL'},
            {'product_id': 14, 'sku_id': 'D-0001-0004', 'unit_price': 2300, 'product_name': 'Dell-4',
             'category': 'DELL'},
            {'product_id': 15, 'sku_id': 'D-0001-0005', 'unit_price': 2400, 'product_name': 'Dell-5',
             'category': 'DELL'},
            {'product_id': 16, 'sku_id': 'D-0001-0006', 'unit_price': 2500, 'product_name': 'Dell-6',
             'category': 'DELL'},
            {'product_id': 17, 'sku_id': 'D-0001-0007', 'unit_price': 2600, 'product_name': 'Dell-7',
             'category': 'DELL'},
            {'product_id': 18, 'sku_id': 'D-0001-0008', 'unit_price': 2700, 'product_name': 'Dell-8',
             'category': 'DELL'},
            {'product_id': 19, 'sku_id': 'D-0001-0009', 'unit_price': 2800, 'product_name': 'Dell-9',
             'category': 'DELL'},
            {'product_id': 20, 'sku_id': 'D-0001-0010', 'unit_price': 2900, 'product_name': 'Dell-10',
             'category': 'DELL'},
            {'product_id': 21, 'sku_id': 'L-0001-0001', 'unit_price': 2000, 'product_name': 'LG-1', 'category': 'LG'},
            {'product_id': 22, 'sku_id': 'L-0001-0002', 'unit_price': 2100, 'product_name': 'LG-2', 'category': 'LG'},
            {'product_id': 23, 'sku_id': 'L-0001-0003', 'unit_price': 2200, 'product_name': 'LG-3', 'category': 'LG'},
            {'product_id': 24, 'sku_id': 'L-0001-0004', 'unit_price': 2300, 'product_name': 'LG-4', 'category': 'LG'},
            {'product_id': 25, 'sku_id': 'L-0001-0005', 'unit_price': 2400, 'product_name': 'LG-5', 'category': 'LG'},
            {'product_id': 26, 'sku_id': 'L-0001-0006', 'unit_price': 2500, 'product_name': 'LG-6', 'category': 'LG'},
            {'product_id': 27, 'sku_id': 'L-0001-0007', 'unit_price': 2600, 'product_name': 'LG-7', 'category': 'LG'},
            {'product_id': 28, 'sku_id': 'L-0001-0008', 'unit_price': 2700, 'product_name': 'LG-8', 'category': 'LG'},
            {'product_id': 29, 'sku_id': 'L-0001-0009', 'unit_price': 2800, 'product_name': 'LG-9', 'category': 'LG'},
            {'product_id': 30, 'sku_id': 'L-0001-0010', 'unit_price': 2900, 'product_name': 'LG-10', 'category': 'LG'},
            {'product_id': 31, 'sku_id': 'S-0001-0001', 'unit_price': 2000, 'product_name': 'SAMSUNG-1',
             'category': 'SAMSUNG'},
            {'product_id': 32, 'sku_id': 'S-0001-0002', 'unit_price': 2100, 'product_name': 'SAMSUNG-2',
             'category': 'SAMSUNG'},
            {'product_id': 33, 'sku_id': 'S-0001-0003', 'unit_price': 2200, 'product_name': 'SAMSUNG-3',
             'category': 'SAMSUNG'},
            {'product_id': 34, 'sku_id': 'S-0001-0004', 'unit_price': 2300, 'product_name': 'SAMSUNG-4',
             'category': 'SAMSUNG'},
            {'product_id': 35, 'sku_id': 'S-0001-0005', 'unit_price': 2400, 'product_name': 'SAMSUNG-5',
             'category': 'SAMSUNG'},
            {'product_id': 36, 'sku_id': 'S-0001-0006', 'unit_price': 2500, 'product_name': 'SAMSUNG-6',
             'category': 'SAMSUNG'},
            {'product_id': 37, 'sku_id': 'S-0001-0007', 'unit_price': 2600, 'product_name': 'SAMSUNG-7',
             'category': 'SAMSUNG'},
            {'product_id': 38, 'sku_id': 'S-0001-0008', 'unit_price': 2700, 'product_name': 'SAMSUNG-8',
             'category': 'SAMSUNG'},
            {'product_id': 39, 'sku_id': 'S-0001-0009', 'unit_price': 2800, 'product_name': 'SAMSUNG-9',
             'category': 'SAMSUNG'},
            {'product_id': 40, 'sku_id': 'S-0001-0010', 'unit_price': 2900, 'product_name': 'SAMSUNG-10',
             'category': 'SAMSUNG'},
            {'product_id': 41, 'sku_id': 'Y-0001-0001', 'unit_price': 2000, 'product_name': 'Sony-1',
             'category': 'SONY'},
            {'product_id': 42, 'sku_id': 'Y-0001-0002', 'unit_price': 2100, 'product_name': 'Sony-2',
             'category': 'SONY'},
            {'product_id': 43, 'sku_id': 'Y-0001-0003', 'unit_price': 2200, 'product_name': 'Sony-3',
             'category': 'SONY'},
            {'product_id': 44, 'sku_id': 'Y-0001-0004', 'unit_price': 2300, 'product_name': 'Sony-4',
             'category': 'SONY'},
            {'product_id': 45, 'sku_id': 'Y-0001-0005', 'unit_price': 2400, 'product_name': 'Sony-5',
             'category': 'SONY'},
            {'product_id': 46, 'sku_id': 'Y-0001-0006', 'unit_price': 2500, 'product_name': 'Sony-6',
             'category': 'SONY'},
            {'product_id': 47, 'sku_id': 'Y-0001-0007', 'unit_price': 2600, 'product_name': 'Sony-7',
             'category': 'SONY'},
            {'product_id': 48, 'sku_id': 'Y-0001-0008', 'unit_price': 2700, 'product_name': 'Sony-8',
             'category': 'SONY'},
            {'product_id': 49, 'sku_id': 'Y-0001-0009', 'unit_price': 2800, 'product_name': 'Sony-9',
             'category': 'SONY'},
            {'product_id': 50, 'sku_id': 'Y-0001-0010', 'unit_price': 2900, 'product_name': 'Sony-10',
             'category': 'SONY'}
        ]

        # orders와 order_items 데이터를 저장할 리스트
        orders_data = []
        order_items_data = []

        # 기준일 설정
        base_date = datetime(2025, 1, 1)

        print("데이터 생성 중...")
        for i in tqdm(range(1, num_records + 1)):
            # 랜덤 날짜 생성
            days_offset = random.randint(0, 364)
            hours_offset = random.randint(0, 23)
            minutes_offset = random.randint(0, 59)

            created_at = base_date + timedelta(days=days_offset, hours=hours_offset, minutes=minutes_offset)
            expire_time = created_at + timedelta(days=7)
            date_path = format(days_offset, '05x')

            # 랜덤 값 생성
            user_id = random.randint(1, 100)

            # 구매할 상품 아이템 개수 결정 (1~3개)
            num_items = random.randint(1, 3)

            # 주문 아이템 생성 및 총액 계산
            order_items = []
            total_price = 0
            total_ea = 0

            # num_items 개의 상품 선택
            selected_products = random.sample(products, num_items)

            for product in selected_products:
                ea = random.randint(1, 5)  # 각 상품마다 1~5개 구매
                item_price = product['unit_price'] * ea

                order_items.append({
                    'product_id': product['product_id'],
                    'sku_id': product['sku_id'],
                    'ea': ea,
                    'unit_price': product['unit_price'],
                    'item_price': item_price
                })

                total_price += item_price
                total_ea += ea

            # 할인 금액 계산 (총액의 0~10%)
            discount_rate = random.randint(0, 10) / 100
            discount_amount = int(total_price * discount_rate)
            final_amount = total_price - discount_amount

            # orders 데이터 추가
            orders_data.append((
                discount_amount,
                final_amount,
                total_ea,
                total_price,
                date_path,
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                expire_time.strftime('%Y-%m-%d %H:%M:%S'),
                user_id,
                'PAID'
            ))

            # order_items 데이터 추가
            for item in order_items:
                order_items_data.append((
                    item['ea'],
                    i,  # order_id
                    item['unit_price'],
                    item['sku_id']
                ))

        # orders 데이터 삽입
        print("\norders 테이블에 데이터 삽입 중...")
        orders_insert_query = """
                              INSERT INTO orders
                              (discount_amount, final_amount, total_ea, total_price, date_path,
                               created_at, expire_time, user_id, status)
                              VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s) \
                              """
        cursor.executemany(orders_insert_query, orders_data)

        # order_items 데이터 삽입
        print("order_items 테이블에 데이터 삽입 중...")
        items_insert_query = """
                             INSERT INTO order_items
                                 (ea, order_id, unit_price, sku_id)
                             VALUES (%s, %s, %s, %s) \
                             """
        cursor.executemany(items_insert_query, order_items_data)

        # 변경사항 커밋
        conn.commit()

        print(f"\n데이터 삽입 완료:")
        print(f"- Orders: {len(orders_data)}건")
        print(f"- Order Items: {len(order_items_data)}건")

        return {
            'success': True,
            'message': '데이터 생성 및 삽입 완료',
            'total_orders': len(orders_data),
            'total_order_items': len(order_items_data)
        }

    except Exception as e:
        if 'conn' in locals():
            conn.rollback()
        return {
            'success': False,
            'message': f'오류 발생: {str(e)}'
        }

    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals() and conn.is_connected():
            conn.close()


# 10만건의 데이터 생성 및 삽입 실행
result = generate_and_insert_dummy_data(100000)
print(result)