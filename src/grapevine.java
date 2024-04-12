import java.util.*;

public class grapevine {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        HashMap<String, BFSTree<Person>> namePerson = new HashMap<>();
        String[] input = s.nextLine().split(" ");
        int persons_n = Integer.parseInt(input[0]);
        int connections_m = Integer.parseInt(input[1]);
        int days_d = Integer.parseInt(input[2]);

        if(persons_n == 0){
            System.out.println(0);
            return;
        }
        if((connections_m == 0 || days_d == 0) && persons_n > 0){
            System.out.println(1);
            return;
        }

        //Array containing every line in the scanner
        String[] names = new String[persons_n + connections_m + 1];
        for (int i = 0; i <= persons_n + connections_m; i++) {
            names[i] = s.nextLine();
        }
        s.close();

        //initializing the namePerson hashmap.
        for (int i = 0; i < persons_n; i++) {
            Person person = new Person(Integer.parseInt(names[i].split(" ")[1]), names[i].split(" ")[0]);
            namePerson.put(person.name, BFSTree.of(person));
        }

        //initializes connections.
        Queue<String> queue = new ArrayDeque<>(Arrays.asList(names).subList(persons_n, connections_m + persons_n));
        while(!queue.isEmpty()){
            String[] connect = queue.remove().split(" ");
            namePerson.get(connect[0]).addChild(namePerson.get(connect[1]));
        }

        BFSTree<Person> fp = namePerson.get(names[persons_n+connections_m]);
        
        Queue<BFSTree<Person>> head = new ArrayDeque<>();
        head.add(fp);
        int known = -1;

        for(int i = 0; i < days_d; i++){
            BFSTree<Person> currentP = head.remove();
            if(currentP.getValue().telling){
                currentP.getValue().Rumour(currentP.children);
                head.add(namePerson.get(names[persons_n + connections_m]));
                for(BFSTree<Person> child : currentP.children){
                    if(child.getValue().telling) head.add(child);
                }
            }
        }

        for(BFSTree<Person> peoples : namePerson.values()){
            if(peoples.getValue().heard) {
                known++;
            }
        }
        if(known == -1){
            System.out.println(0);

        } else{
            System.out.println(known);
        }
    }
}
class BFSTree<T> {
    private final T value;
    Set<BFSTree<T>> children;

    BFSTree(T value){
        this.value = value;
        this.children = new HashSet<>();
    }

    public T getValue(){
        return value;
    }
    public Set<BFSTree<T>>getChildren(){
        return children;
    }

    public static <T> BFSTree<T> of(T value) {
        return new BFSTree<>(value);
    }
    public void addChild(BFSTree<T> newChild) {
        children.add(newChild);
    }
    public static <T> Optional<BFSTree<T>> search(T value, BFSTree<T> root) {
        Queue<BFSTree<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BFSTree<T> currentNode = queue.remove();
            if (currentNode.getValue().equals(value)) {
                return Optional.of(currentNode);
            } else {
                queue.addAll(currentNode.getChildren());
            }
        }
        return Optional.empty();
    }
}

class Person{
    ArrayList<Person> heardFrom = new ArrayList<>();
    int scepticism;
    public String name;
    boolean telling;
    boolean heard;
    public Person(int scepticism, String name){
        this.scepticism = scepticism;
        this.name = name;
        if(scepticism == 0) {
            telling = true;
            heard = true;
        } else {
            telling = false;
            heard = false;
        }
    }
    void Rumour(Set<BFSTree<Person>> adjPerson){
        for(BFSTree<Person> personBFSTree : adjPerson){
            if(personBFSTree.getValue().heardFrom.contains(this)) return;
            personBFSTree.getValue().heard = true;
            if(personBFSTree.getValue().scepticism > 0){
                personBFSTree.getValue().scepticism--;
            }
            if(personBFSTree.getValue().scepticism == 0) personBFSTree.getValue().telling = true;

            personBFSTree.getValue().heardFrom.add(this);
        }
    }
}
