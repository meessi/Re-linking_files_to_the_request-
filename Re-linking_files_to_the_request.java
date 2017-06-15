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

/*
*	Метод перепривязки файлов к запросу
*
*/
def mapOfFiles =
{
  	mapOfFiles_ ->
  
  	def size = mapOfFiles_.size();
  	def i = 0
  
  	for (; i < size; i++)
  	{
      	api.tx.call
      	{
         	utils.edit(mapOfFiles_[i], ['relation' : null]); 	
        }
    }
  
  	return i;
}

/*
*	Основной метод
*
*/
def process =
{
  	def request =
    """
	select
		new map(file.source
				,substring(file.relation, locate(\':\', file.relation) + 1, len(file.relation)))
	from
		file file
	where
		file.relation is not null
		and file.source like \'serviceCall\$%\'
		and file.relation != 'serviceCall/attribute:descriptionInRTF'
		and file.relation != 'serviceCall/attribute:addComment'
	""";
  
  	def mapOfRequests = sql(request);
  	def size = mapOfRequests.size();
  	def mapOfFiles_ = [];
  
  	if (size != 0)
  	{
      	for (def i = 0; i < size; i++)
      	{
          	def subject = utils.get('serviceCall', ['UUID' : mapOfRequests[i][0.toString()]]);
          	def files = mapOfRequests[i][1.toString()].toString();
          	def size_ = subject != null ? subject[files].size() : 0;
          
          	if (subject != null && size_ != 0)
          	{
              	for (def j = 0; j < size_; j++)
              	{
                  	mapOfFiles_ << subject[files][j];
                }	
            }
        }
    }
  
  	return mapOfFiles(mapOfFiles_);
}

if (true)
{
  	return process();
}