package lcn;


import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import utils.EditDistance;


public class EntitySearcher 
{
	public IndexSearcher searcher = null;
	public QueryParser parser = null;
	public Analyzer analyzer = null;
	public static String basePath = "E:\\Husen\\Code\\workspace\\gAnswerChinese\\";
	public static String indexPath = basePath+"data/index/entity_index";
	public HashSet<String> stopWordSet = null;
	public EntitySearcher()
	{
		try
		{
			searcher = new IndexSearcher(indexPath);
			analyzer = new StandardAnalyzer();
			parser = new QueryParser("EntityName", analyzer);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		stopWordSet = new HashSet<String>();
		stopWordSet.add("��Ӱ");
		stopWordSet.add("ϲ��");
		stopWordSet.add("����Ƭ");
		stopWordSet.add("�ֲ�Ƭ");
		stopWordSet.add("����");
		stopWordSet.add("��Ա");
	}
	
	public String searchEntity(String queryString) 
	{
		String entityName = null, entityType = null;
		double maxScore = 0.0;
		
		if (stopWordSet.contains(queryString))
			return null;
		try
		{
			Query query = parser.parse(queryString);
			
			Hits hits = searcher.search(query);				
			for (int i=0;i<hits.length() && i<20;i++)
				if (hits.score(i) > maxScore || EditDistance.calcutateEditDistance(queryString, hits.doc(i).get("EntityName"))==0)
				{
					maxScore = hits.score(i);
					entityName = hits.doc(i).get("EntityName");
					entityType = hits.doc(i).get("EntityType");
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(entityName);
		
		if (maxScore >= 0.7 && EditDistance.calcutateEditDistance(queryString, entityName) * 4 <= queryString.length())			
			return entityName;
		else
			return null;
	}
	
	public String searchEntityAndType(String queryString) 
	{
		String entityName = null, entityType = null;
		double maxScore = 0.0;
		
		if (stopWordSet.contains(queryString))
			return null;
		try
		{
			Query query = parser.parse(queryString);
			
			Hits hits = searcher.search(query);				
			for (int i=0;i<hits.length() && i<20;i++)
				if (hits.score(i) > maxScore|| EditDistance.calcutateEditDistance(queryString, hits.doc(i).get("EntityName"))==0)
				{
					maxScore = hits.score(i);
					entityName = hits.doc(i).get("EntityName");
					entityType = hits.doc(i).get("EntityType");
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (maxScore >= 0.7 && EditDistance.calcutateEditDistance(queryString, entityName) * 4 <= queryString.length())
			return entityName + "-" + entityType;			
		else
			return null;
	}
	
	public static void main(String[] args)
	{
		
		
		EntitySearcher es = new EntitySearcher();
		System.out.println(EditDistance.calcutateEditDistance("������ҵ","������ҳ"));
		//String ans = es.searchEntity("������");
		long t  = System.currentTimeMillis();
		String ans = es.searchEntityAndType("��ΰ");
		System.out.println(ans);
		ans = es.searchEntityAndType("���ȡ������ȣ���Ӱ��");
		System.out.println(ans);
		ans = es.searchEntityAndType("�׷�ħŮ��֮����");
		System.out.println(ans);
		System.out.println("time="+(System.currentTimeMillis()-t));
		
	}

}
