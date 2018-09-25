
-- 5.1
SELECT category, COUNT(*) as cnt FROM product GROUP BY category ORDER BY cnt DESC LIMIT 10;

-- 5.2
SELECT tp.name, tp.category, count(*) as cnt FROM product tp inner join
(select category, count(*) as c from product group by category order by c desc) tcat
ON tp.category = tcat.category
GROUP BY tp.name, tp.category ORDER BY cnt DESC LIMIT 10;

-- 6.3 show ip only
SELECT t.ip, sum(t.price) sump FROM product t GROUP BY t.ip ORDER BY sump DESC LIMIT 10;
