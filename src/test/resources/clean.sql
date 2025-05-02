SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM coupon;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM payments;
DELETE FROM stock;
DELETE FROM user;
DELETE FROM user_coupons;

INSERT INTO user (amount, user_id, version) VALUES (100000, 1, 0);
INSERT INTO coupon (coupon_id, discount_amount, init_quantity, remain_quantity, coupon_name, discount_type, expire_time) VALUES (1, 1000, 10, 10, '고정쿠폰', 'FIXED_AMOUNT', '2025-05-20 02:13:54.702128');
INSERT INTO coupon (coupon_id, discount_amount, init_quantity, remain_quantity, coupon_name, discount_type, expire_time) VALUES (2, 10, 10, 10, '퍼센트쿠폰', 'PERCENTAGE', '2025-05-20 02:13:54.702128');


SET FOREIGN_KEY_CHECKS = 1;