package com.utils.tree.kdtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class KDTree {

  private static final int LNG = 0;
  private static final int LAT = 1;
  private static final int K = 2;

  List<Point> list;
  KDNode root;

  public KDTree(List<Point> list) {
    this.list = list;
    this.root = createNode(list, 0);
  }

  private KDNode createNode(List<Point> list, int deep) {
    if (isEmpty(list)) {
      return null;
    }

    int split = deep % K;
    if (LNG == split) {
      Collections.sort(list, LNG_COMPARATOR);
    } else if (LAT == split) {
      Collections.sort(list, LAT_COMPARATOR);
    }

    int size = list.size();
    int mid = size / 2;
    KDNode node = new KDNode(deep, list.get(mid));

    List<Point> leftBuff = new ArrayList<Point>(size - 1);
    List<Point> rightBuff = new ArrayList<Point>(size - 1);
    for (int i = 0; i < size; i++) {
      if (mid == i) {
        continue;
      }

      Point point = list.get(i);
      if (compareTo(split, point, node.data) <= 0) {
        leftBuff.add(point);
      } else {
        rightBuff.add(point);
      }
    }

    if ((mid - 1) >= 0 && leftBuff.size() > 0) {
      node.left = createNode(leftBuff, deep + 1);
      node.left.parent = node;
    }

    if ((mid + 1) <= (list.size() - 1) && rightBuff.size() > 0) {
      node.right = createNode(rightBuff, deep + 1);
      node.right.parent = node;
    }
    return node;
  }

  public KDTree addNode(Point point) {
    if (root == null) {
      root = new KDNode(0, point);
      return this;
    }

    KDNode node = root;
    while (true) {
      int split = node.deep % K;
      if (compareTo(split, point, node.data) <= 0) {
        if (node.left == null) {
          KDNode _n = new KDNode(node.deep + 1, point);
          _n.parent = node;
          node.left = _n;
          break;
        }
        node = node.left;
      } else {
        if (node.right == null) {
          KDNode _n = new KDNode(node.deep + 1, point);
          _n.parent = node;
          node.right = _n;
          break;
        }
        node = node.right;
      }
    }
    return this;
  }

  public KDNode getNode(Point point) {
    if (root == null) {
      return null;
    }

    if (root.data.equals(point)) {
      return root;
    }
    return getNode(root, point);
  }

  public Collection<KDNode> getNearestPoint(Point point, int size) {
    TreeSet<KDNode> set = new TreeSet<KDNode>(new Comparator<KDNode>(){
      @Override
      public int compare(KDNode o1, KDNode o2) {
        if(o1.equals(o2)) {
          return 0;
        }
        int compare = o1.distance - o2.distance;
        return compare<=0 ? -1 : 1;
      }
    });

    int distance = distance(point, root.data);
    root.distance = distance;
    set.add(root);
    searchNearestNode(root, point, set, size);
    return set;
  }

  public KDNode getNearestPoint(Point point) {
    int distance = distance(point, root.data);
    return getNearestNode(root, point, distance);
  }

  private KDNode getNearestNode(KDNode node, Point point, int distance) {
    int split = node.deep % K;
    if (compareTo(split, point, node.data) <= 0) {
      if (node.left != null) {
        int leftDistance = distance(point, node.left.data);
        if (leftDistance <= distance) {
          node.left.distance = leftDistance;
          return node.left;
        }
        return getNearestNode(node.left, point, leftDistance);
      }
    } else {
      if (node.right != null) {
        int rightDistance = distance(point, node.right.data);
        if (rightDistance <= distance) {
          node.right.distance = rightDistance;
          return node.right;
        }
        return getNearestNode(node.right, point, rightDistance);
      }
    }
    return null;
  }

  private void searchNearestNode(KDNode node, Point point, TreeSet<KDNode> set, int size) {
    if (node.left != null) {
      int leftDistance = distance(point, node.left.data);
      if (leftDistance <= set.last().distance || (set.size() < size)) {
        if (set.size() >= size) {
          set.remove(set.last());
        }
        node.left.distance = leftDistance;
        set.add(node.left);
      }
      searchNearestNode(node.left, point, set, size);
    }
    if (node.right != null) {
      int rightDistance = distance(point, node.right.data);
      if ((rightDistance <= set.last().distance || (set.size() < size))) {
        if (set.size() >= size) {
          set.remove(set.last());
        }
        node.right.distance = rightDistance;
        set.add(node.right);
      }
      searchNearestNode(node.right, point, set, size);
    }
  }

  private KDNode getNode(KDNode node, Point point) {
    int split = node.deep % K;
    if (compareTo(split, point, node.data) <= 0) {
      if (node.left != null && node.left.equals(point)) {
        return node.left;
      } else {
        return getNode(node.left, point);
      }
    } else {
      if (node.right != null && node.right.equals(point)) {
        return node.right;
      } else {
        return getNode(node.right, point);
      }
    }
  }

  private int compareTo(int split, Point o1, Point o2) {
    if (LNG == split) {
      return LNG_COMPARATOR.compare(o1, o2);
    } else if (LAT == split) {
      return LAT_COMPARATOR.compare(o1, o2);
    }
    return 0;
  }

  private static int distance(Point p1, Point p2) {
    return distance(p1.getLongitude(), p1.getLatitude(), p2.getLongitude(), p2.getLatitude());
  }

  private static int distance(double lng1, double lat1, double lng2, double lat2) {
    double radLat1 = Math.toRadians(lat1);
    double radLat2 = Math.toRadians(lat2);
    double radLng1 = Math.toRadians(lng1);
    double radLng2 = Math.toRadians(lng2);
    double deltaLat = radLat1 - radLat2;
    double deltaLng = radLng1 - radLng2;
    double distance =
        2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
            * Math.pow(Math.sin(deltaLng / 2), 2)));
    distance = distance * 6378.137;
    distance = Math.round(distance * 10000) / 10;
    return (int) distance;
  }

  private <T> boolean isEmpty(Collection<T> value) {
    if (value == null) {
      return true;
    }
    if (value.size() == 0) {
      return true;
    }
    return false;
  }

  public class KDNode {
    int deep = 0;
    Point data;
    KDNode parent;
    KDNode left;
    KDNode right;

    int distance;

    public KDNode(int deep, Point data) {
      this.deep = deep;
      this.data = data;
    }
  }

  private static final Comparator<Point> LNG_COMPARATOR = new Comparator<Point>() {
    @Override
    public int compare(Point o1, Point o2) {
      if (o1.getLongitude() < o2.getLongitude()) {
        return -1;
      }
      if (o1.getLongitude() > o2.getLongitude()) {
        return 1;
      }
      return 0;
    }
  };

  private static final Comparator<Point> LAT_COMPARATOR = new Comparator<Point>() {
    @Override
    public int compare(Point o1, Point o2) {
      if (o1.getLatitude() < o2.getLatitude()) {
        return -1;
      }
      if (o1.getLatitude() > o2.getLatitude()) {
        return 1;
      }
      return 0;
    }
  };

}
