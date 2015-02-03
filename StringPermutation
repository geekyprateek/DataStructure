package string;

class Permutation{
	String s;
	int count=0;
	
	Permutation(String s){
		this.s=s;
	}
	
	void findPermutation(String s,int k,int n){
		
		if(k==0){
			System.out.print(s);
			System.out.println(this.count++);
			return;
		}
		
		for(int i=0;i<n;i++){
			String local=s+this.s.charAt(i);
			findPermutation(local, k-1, n);
			
		}
	}
	
	StringBuilder swap(int i,int j,StringBuilder s){
		char s1=s.charAt(i);
		char s2=s.charAt(j);
		
		s.setCharAt(j, s1);
		s.setCharAt(i, s2);
		
		return s;
		
	}
	
	void printPermut(int j,StringBuilder s){
		
		if(j==s.length()){
			System.out.println(s);
			return;
		}
		
		for(int i=j;i<s.length();i++){
			swap(i,j,s);
			printPermut(j+1, s);
			swap(i,j,s);
		}
	}
	
	void paranthPermut(int pos,int open,int close, char s[],int length){
		
		if(close==length){
			System.out.println(s);
		}
		else{
			if(open>close){
				char ch='}';
				s[pos]=ch;
				paranthPermut(pos+1, open, close+1, s, length);
			}
			if(open<length){
				char ch='{';
				s[pos]=ch;
				paranthPermut(pos+1, open+1, close, s, length);
			}
			
		}
	}
	
	void isSubsequence(String string){
		//O(n) solution
		int i=0;
		int j=0;
		while(i<s.length() && j<string.length() ){
			
			if(string.charAt(j)==s.charAt(i)){
				j++;
			}
			i++;
		}
		
		if(j==string.length())
			System.out.println("is subsequence");
		else
			System.out.println("not subsequence");
	}
}


public class printAllPermutations {
	
	public static void main (String a[]) {
		char s[]=new char[50];
		Permutation p=new Permutation("abcd");
		System.out.println("total permutation with repeatition of characters for given length k");
		p.findPermutation(new String(""), 4, 4);
		
		System.out.println("total permutation without repeatition of characters");
		p.printPermut(0,new StringBuilder("abcde"));
		
		System.out.println("Balanced paranthesis permutations");
		p.paranthPermut(0, 0, 0, s, 4);
		
		p.isSubsequence("dca");
	}

}
