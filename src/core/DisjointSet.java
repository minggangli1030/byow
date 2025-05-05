package core;

public class DisjointSet {
    int parent[];
    int size[];

    public DisjointSet(int n) {
        parent = new int [n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return;
        if (size[rootA] < size[rootB]) {
            parent[rootA] = rootB;
            size[rootB] += size[rootA];
        } else {
            parent[rootB] = rootA;
            size[rootA] += size[rootB];
        }
    }

    public boolean isConnected(int a, int b) {
        return find(a) == find(b);
    }

    public boolean isAllConnected() {
        int rep = find(0);
        for (int i = 0; i < parent.length; i++) {
            if (find(i) != rep) {
                return false;
            }
        }
        return true;
    }
}
