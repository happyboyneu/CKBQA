package structs;

public class Word implements Comparable<Word> {
	public String baseForm = null;
	public String originalForm = null;
	public String posTag = null;
	public int position = -1;	// �����е�һ���ʵ�position��1. ��ɲμ�������﷨��������[]�е�����.
	public String key = null;
	
	public boolean isCovered = false;
	public boolean isIgnored = false;
	
	public String ner = null;	// ��¼ner�Ľ��������ne��Ϊnull
	public Word nnNext = null;	// ��¼nn���δʵĺ�һ���ʣ���ֹ��null����
	public Word nnPrev = null;	// ��¼nn���δʵ�ǰһ���ʣ���ֹ��null����
	public Word crr	= null;		// ��¼ָ�������ָ��ֻ��¼�ڶ����head�ϣ�ָ����һ�������head
	
	public Word (String base, String original, String pos, int posi) {
		baseForm = base;
		originalForm = original;
		posTag = pos;
		position = posi;		
		key = new String(originalForm+"["+position+"]");
	}
	
	@Override
	public String toString() {
		return key;
	}

	public int compareTo(Word another) {
		return this.position-another.position;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Word) 
			&& originalForm.equals(((Word)o).originalForm)
			&& position == ((Word)o).position;
	}
	
	// С��NnHead����nn�ṹ�Ķ���
	public Word getNnHead() {
		Word w = this;
		while (w.nnPrev != null) {
			w = w.nnPrev;
		}
		return w;
	}
	
	public String getFullEntityName() {
		Word w = this.getNnHead();
		StringBuilder sb = new StringBuilder("");
		while (w != null) {
			sb.append(w.originalForm);			
			sb.append(' ');
			w = w.nnNext;
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String getBaseFormEntityName() {
		Word w = this.getNnHead();
		StringBuilder sb = new StringBuilder("");
		while (w != null) {
			sb.append(w.baseForm);
			sb.append(' ');
			w = w.nnNext;
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String isNER () {
		return this.getNnHead().ner;
	}
	
	public void setIsCovered () {
		Word w = this.getNnHead();
		while (w != null) {
			w.isCovered = true;
			w = w.nnNext;
		}
	}	
}
