/*
*	Метод обработки sql-запроса
*
*/
def sql =
{
  	request ->
  	
  	def query = api.db.query(request);
  	def objects = query.list();
  
  	return objects;
}