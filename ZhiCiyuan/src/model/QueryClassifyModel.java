package model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.fudan.nlp.parser.dep.DependencyTree;
import nlp.DependencyTreeCore;

public class QueryClassifyModel 
{
	public static ParaphraseModel pm;
	public enum queryType{askMovie,askPeople,askAttribute,unresolved};
	public queryType type;
	public boolean containInstanceMovie = false;
	public boolean containInstancePeople = false;
	public boolean containConceptMovie = false;
	public boolean containConceptPeople = false;
	public boolean containPredicateMovie = false;
	public boolean containPredicatePeople = false;
	public boolean containPredicateM2P = false;
	public ArrayList<String> moviePredicateList = new ArrayList<String>();
	public ArrayList<String> peoplePredicateList = new ArrayList<String>();
	public ArrayList<String> m2pPredicateList = new ArrayList<String>();
	
	public QueryClassifyModel(ParaphraseModel pM) 
	{
		QueryClassifyModel.pm = pM;
	}

	void init()
	{
		containInstanceMovie = false;
		containInstancePeople = false;
		containConceptMovie = false;
		containConceptPeople = false;
		containPredicateMovie = false;
		containPredicatePeople = false;
		containPredicateM2P = false;
	//	�������������Ա𡢼�ͥ��Ա���������ڡ������ء�ְҵ���������ڡ����
	//  Ƭ�������֡���ӳ���ڡ����ݡ�Ƭ������������硢���ԡ����͡����ݡ����
	//  imdbID���ײ�������Ƭ�����������ٷ���վ������
		moviePredicateList = new ArrayList<String>(Arrays.asList("Ƭ��","����","��ӳ����","����","Ƭ��"
				,"����","���","����","����","����","���","imdbID","�ײ�","����Ƭ��","����","�ٷ���վ","����"));
		peoplePredicateList = new ArrayList<String>(Arrays.asList("����","����","�Ա�","��ͥ��Ա","��������","������"
				,"ְҵ","��������","���","imdbID"));
		m2pPredicateList = new ArrayList<String>(Arrays.asList("����","����","���"));
	}
	
	public queryType QueryClassify(DependencyTreeCore dtc)
	{
		init();
		
		DependencyTree odt = dtc.odt;
		String[] dsWordArray = odt.toString().split("\n");
		
		for(String row: dsWordArray)
		{
			String[] tmpArray = row.split(" ");
			int id = Integer.parseInt(tmpArray[0]);
			String name = tmpArray[1];
			String posTag = tmpArray[2];
			
			// ����ֱ���� �ִʵ�posTag�����жϺ�������entity searcher��paraphraseModel�ж��ǲ��Ǹ���
			if(posTag.equals("����"))
				containInstancePeople = true;
			if(posTag.equals("Ӱ����"))
				containInstanceMovie = true;
			
			// ���� �����ݡ� ���ִ���Ϊ�� ��Ӱ����������ʵ�ϡ����ݡ����� ν�ʡ���Ӱ-���Ҳ�� ���ʹʡ������Ӧ�ö���
			if(posTag.equals("���ʹ�[��Ӱ]"))
				containConceptMovie = true;
			if(posTag.equals("���ʹ�[����]"))
				containConceptPeople = true;
			
			
			if(pm.getRelatedRelation(name) != null)
			{
				String pre = pm.getRelatedRelation(name).item.name;
				if(moviePredicateList.contains(pre))
					containPredicateMovie = true;
				if(peoplePredicateList.contains(pre))
					containPredicatePeople = true;
				if(m2pPredicateList.contains(pre))
					containPredicateM2P = true;
			}
		}
		
		System.out.println("InstanceMovie:"+containInstanceMovie);
		System.out.println("InstancePeople:"+containInstancePeople);
		System.out.println("ConceptMovie:"+containConceptMovie);
		System.out.println("ConceptPeople:"+containConceptPeople);
		System.out.println("PredicateMovie:"+containPredicateMovie);
		System.out.println("PredicatePeople:"+containPredicatePeople);
		System.out.println("PredicateM2P:"+containPredicateM2P);
		
		//�����Ӱʵ���е�������ʵ���ıߣ�������Ϊ��askMovie
		if(containInstanceMovie && containInstancePeople)
			type = queryType.unresolved;
		else if(containInstanceMovie)
		{
			if(containConceptPeople || containPredicateM2P)
				type = queryType.askPeople;
			else if(containPredicateMovie)
				type = queryType.askAttribute;
			else
				type = queryType.askMovie;
		}
		else if(containInstancePeople)// ���￼�Ǽ�������ν��[����-��Ӱ],��<����>
		{
			if(containConceptMovie)
				type = queryType.askMovie;
			else if(containPredicatePeople)
				type = queryType.askAttribute;
			else
				type = queryType.askPeople;
		}
		else if(containConceptMovie && containConceptPeople)
			type = queryType.unresolved;
		else if(containConceptMovie)
			type = queryType.askMovie;
		else if(containConceptPeople)
			type = queryType.askPeople;
		else
			type = queryType.unresolved;
		
		return type;
	}
}
