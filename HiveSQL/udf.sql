-- using UDF (change according your jar-name)
delete jar /home/cloudera/my/b-0.0.1.jar
add jar /home/cloudera/my/b-0.0.1.jar
create temporary function getStartIP as 'a.udf.GetStartIP'
create temporary function getEndIP as 'a.udf.GetEndIP'
create temporary function getIP as 'a.udf.GetIP'