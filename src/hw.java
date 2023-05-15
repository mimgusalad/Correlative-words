import java.io.File;
import java.io.IOException;
import java.util.*;

class CompFreq implements Comparator<Map.Entry<String, Integer>>{

    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
    }
}

class CompPairFreq implements Comparator<Map.Entry<Pair, Integer>>{

    @Override
    public int compare(Map.Entry<Pair, Integer> o1, Map.Entry<Pair, Integer> o2) {
        int cmp = o2.getValue().compareTo(o1.getValue());
        if(cmp == 0){
            int cmp_word = o1.getKey().word1.compareTo(o2.getKey().word1);
            if(cmp_word == 0){
                return o1.getKey().word2.compareTo(o2.getKey().word2);
            }else{
                return cmp_word;
            }
        }else{
            return cmp;
        }
    }
}
class Pair implements Comparator<Pair>{
    String word1;
    String word2;

    public Pair(String s1, String s2) {
        if(s1.compareTo(s2) < 0) {
            word1 = s1;
            word2 = s2;
        }else {
            word1 = s2;
            word2 = s1;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(word1, word2);
    }

    @Override
    public int compare(Pair o1, Pair o2) {
        return o1.word1.compareTo(o2.word1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        return Objects.equals(word1, other.word1) && Objects.equals(word2, other.word2);
    }

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }

}

public class hw {
    public static void main(String[] args) throws IOException {
        String filename;
        int k;
        System.out.println("파일 이름? ");
        Scanner sc = new Scanner(System.in);
        filename = sc.next();
        k = sc.nextInt();

        Scanner sc2 = new Scanner(new File("stop.txt"));
        HashSet<String> stopwords = new HashSet<>();

        while(sc2.hasNext()) {
            String word = sc2.next();
            stopwords.add(word);
        }
        stopwords.add("");


        Scanner sc3 = new Scanner(new File(filename));
        //sc3.useDelimiter("[/()\n\\s,.;'\"?!]");
        sc3.useDelimiter("[.?!]");

        HashMap<String, Integer> word_freq = new HashMap<>();
        HashMap<Pair, Integer> pair_freq = new HashMap<>();

        String delimiter = "[/()\n\\s,;'\"]";
        while(sc3.hasNext()){
            String sentence = sc3.next().trim().toLowerCase();
            String[] trim = sentence.split(delimiter);
            HashSet<String> words_for_pair = new HashSet<>();
            for(String token:trim){
                words_for_pair.add(token);
            }


            for(String word: trim){
                if(!stopwords.contains(word)){
                    word_freq.put(word, word_freq.getOrDefault(word, 0)+1);
                }
            }
            words_for_pair.removeAll(stopwords);

            for(String word1:words_for_pair){
                for(String word2:words_for_pair){
                    if(!word1.equals(word2)){
                        Pair pair = new Pair(word1, word2);
                        if(pair_freq.containsKey(pair)){
                            pair_freq.put(pair, pair_freq.getOrDefault(pair,0)+1);
                        }
                        else{
                            pair_freq.put(pair, 1);
                        }
                    }
                }
            }
        }

        for(Map.Entry<Pair, Integer> entry: pair_freq.entrySet()){
            entry.setValue(entry.getValue()/2);
        }


        PriorityQueue<Map.Entry<String, Integer>> word_freq_priority = new PriorityQueue<>(word_freq.size(), new CompFreq());
        PriorityQueue<Map.Entry<Pair, Integer>> pair_freq_priority = new PriorityQueue<>(pair_freq.size(), new CompPairFreq());
        for(Map.Entry<String, Integer> entry:word_freq.entrySet()){
            word_freq_priority.add(entry);
        }

        for(Map.Entry<Pair, Integer> entry: pair_freq.entrySet()){
            pair_freq_priority.add(entry);
        }
        
        SortedSet<Map.Entry<String, Integer>> top_freq_word = new TreeSet<>(new CompFreq());
        SortedSet<Map.Entry<Pair, Integer>> top_freq_pair = new TreeSet<>(new CompPairFreq());

        for(int i=0; i<k; i++){
            //System.out.println(word_freq_priority.remove());
            //System.out.println(pair_freq_priority.remove());
            top_freq_word.add(word_freq_priority.remove());
            top_freq_pair.add(pair_freq_priority.remove());
        }

        System.out.println("Top-k 문자열: "+top_freq_word);
        System.out.println("Top-k 단어쌍: "+top_freq_pair);

        sc.close();
        sc2.close();
    }
}
