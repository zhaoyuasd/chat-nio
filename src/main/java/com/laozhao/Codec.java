package com.laozhao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Codec {

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if(root==null){
            return null;
        }
        List<Integer> list=new ArrayList();
        list.add(root.val);
        List<TreeNode> next=new ArrayList();
        next.add(root.left);
        next.add(root.right);
        serialize(next,list);
        return list.toString();
    }
    public void serialize(List<TreeNode> tmp,List<Integer> result) {
        Boolean shouldNext=false;
        for(TreeNode item: tmp){
            if(item!=null){
                shouldNext=true;
                break;
            }
        }
        if(!shouldNext){
            return;
        }
        List<TreeNode> next=new ArrayList();

        for(TreeNode item: tmp){
            if(item!=null){
                next.add(item.left);
                next.add(item.right);
                result.add(item.val);
            }
            else {
                result.add(null);
            }
        }
        serialize(next,result);
    }



    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if(data==null||"null".equalsIgnoreCase(data)){
            return null;
        }
        String[] st=data.substring(1,data.length()-1).split(",");
        List<String> list=new ArrayList(st.length);
        Collections.addAll(list,st);
        TreeNode root=new TreeNode(Integer.valueOf(list.remove(0)));
        if(list.isEmpty()){
            return  root;
        }
        List<TreeNode> next=new ArrayList();
        String ii=list.remove(0);
        if(isNullstr(ii)){
        }else {
            TreeNode left=new TreeNode(Integer.valueOf(getNum(ii)));
            root.left=left;
            next.add(left);
        }

        if(list.isEmpty()){
            return  root;
        }
        ii=list.remove(0);
        if(isNullstr(ii)) {
        }else {
            TreeNode right=new TreeNode(Integer.valueOf(getNum(ii)));
            root.right=right;
            next.add(right);
        }
        deserialize(next,list);
        return root;
    }

    private boolean isNullstr(String ii) {
        return ii==null||"null".equalsIgnoreCase(ii.trim());
    }

    public void deserialize(List<TreeNode> tmp,List<String> list) {
        if(list.isEmpty()){
            return;
        }
        List<TreeNode> next=new ArrayList();
        for(TreeNode item: tmp){
            if(item==null){
                continue;
            }
            if(list.isEmpty()){
                return;
            }

            String ii=list.remove(0);
            if(isNullstr(ii)){
                //  next.add(null);
            }else {
                TreeNode left = new TreeNode(getNum(ii));
                item.left=left;
                next.add(left);
            }
            if(list.isEmpty()){
                return;
            }
            ii=list.remove(0);
            if(isNullstr(ii)){
                //  next.add(null);
            }else {
                TreeNode right = new TreeNode(getNum(ii));
                item.right=right;
                next.add(right);
            }
        }
        deserialize(next,list);
    }

    private   Integer getNum(String str) {
        if(isNullstr(str)){
            return null;
        }
        return Integer.valueOf(str.trim());
    }

    public static void main(String[] args) {
        Codec codec=new Codec();
        String a="[5,2,3,null,null,2,4,3,1]";
        TreeNode tt=codec.deserialize(a);
        System.out.println(codec.serialize(tt));
    }

}
// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.deserialize(codec.serialize(root));
