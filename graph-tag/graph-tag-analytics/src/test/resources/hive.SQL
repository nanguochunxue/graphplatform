CREATE DATABASE IF NOT EXISTS crm_dev2;
USE crm_dev2;
-------------------------------------------
-- to_balance
-------------------------------------------
DROP TABLE IF EXISTS to_balance;
CREATE EXTERNAL TABLE IF NOT EXISTS to_balance
(
object_key STRING comment '主键',
company_key STRING comment '公司主键',
company_name STRING comment '公司名称',
avg_invest_amount	DOUBLE comment	'平均可投资金额',
deposit_balance	DOUBLE comment	'存款余额',
finance_balance	DOUBLE comment	'理财余额',
loan_balance	DOUBLE comment	'贷款余额',
discount_balance	DOUBLE comment	'贴现余额',
trade_balance	DOUBLE comment	'贸融余额',
bank_acceptance_balance	DOUBLE comment	'银承余额',
credit_amount	DOUBLE comment	'信用证金额',
guarantee_amount	DOUBLE comment	'保函金额',
deposit_ftp_income	DOUBLE comment	'存款FTP收入',
deposit_interest_payment	DOUBLE comment	'存款利息支出',
loan_interest_income	DOUBLE comment	'贷款利息收入',
loan_ftp_cost	DOUBLE comment	'贷款FTP成本',
discount_interest_income	DOUBLE comment	'贴现利息收入',
discount_ftp_cost	DOUBLE comment	'贴现FTP成本',
deposit_year_day_avg	DOUBLE comment	'存款年日均',
loan_year_day_avg	DOUBLE comment	'贷款年日均',
day STRING comment '分区字段-按天'
) COMMENT "余额表"
ROW FORMAT DELIMITED FIELDS TERMINATED BY "#"
LOCATION "/user/graph/tag/data/to_balance";

-------------------------------------------
-- to_active_product
-------------------------------------------
DROP TABLE IF EXISTS to_active_product;
CREATE EXTERNAL TABLE IF NOT EXISTS to_active_product
(
object_key STRING comment '主键',
company_key STRING comment '公司主键',
company_name STRING comment '公司名称',
product_code	STRING comment	'产品代码',
product_name	STRING comment	'产品名称',
product_type	STRING comment	'产品大类：1理财,2定存',
maturity_date	STRING comment	'到期日',
day STRING comment '分区字段-按天'
) COMMENT "有效产品表"
ROW FORMAT DELIMITED FIELDS TERMINATED BY "#"
LOCATION "/user/graph/tag/data/to_active_product";