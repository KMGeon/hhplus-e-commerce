import random
from datetime import datetime, timedelta
import pandas as pd
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
            total_price = random.randint(10000, 100000)
            discount_amount = random.randint(0, 10000)
            total_ea = random.randint(1, 10)
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

            # order_items 데이터 생성
            num_items = random.randint(1, 3)
            for _ in range(num_items):
                sku_id = f"SKU{random.randint(1, 1000):03d}"
                ea = total_ea // num_items
                unit_price = total_price // num_items

                order_items_data.append((
                    ea,
                    i,  # order_id
                    unit_price,
                    sku_id
                ))

        # orders 데이터 삽입
        print("\norders 테이블에 데이터 삽입 중...")
        orders_insert_query = """
        INSERT INTO orders 
        (discount_amount, final_amount, total_ea, total_price, date_path, 
         created_at, expire_time, user_id, status)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.executemany(orders_insert_query, orders_data)

        # order_items 데이터 삽입
        print("order_items 테이블에 데이터 삽입 중...")
        items_insert_query = """
        INSERT INTO order_items 
        (ea, order_id, unit_price, sku_id)
        VALUES (%s, %s, %s, %s)
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