-- create view Product with ip as long
create view if not exists vproduct as 
select tb.ip, getIP(tb.ip) as iip, tb.p as sump
from (SELECT t.ip, sum(t.price) p FROM product t group by t.ip order by p desc limit 10) tb;

-- create view CountryIP with startip as long and endip as long
create view if not exists vcountryip as 
select tb.geoname_id, getStartIP(tb.network) as startip, getEndIP(tb.network) as endip
from countryip tb;

-- 6.3 task: show country name
SELECT t2.sump, t3.geoname_id, t4.country_name FROM vproduct t2, vcountryip t3 
INNER JOIN countryName t4 ON t4.geoname_id = t3.geoname_id 
WHERE t2.iip < t3.endip AND t2.iip > t3.startip LIMIT 10;

