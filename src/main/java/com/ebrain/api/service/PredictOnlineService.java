
/**
 * 在线预测相关业务实现
 */
package com.ebrain.api.service;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class PredictOnlineService {
	 private static class node_t
	    {
	        public int node_id;     
	        public int left = -1;    
	        public int right = -1;  
	        public int miss;        
	        public String gain;       
	        public int feature_id;
	        public double threshold; 
	        public double value;             
	    }

	    private static String task_type = "1";  //
	    private static Vector<HashMap<Integer, node_t>> tree_node_vec = new Vector<HashMap<Integer, node_t>>();
	    private static int treeCount = 0;
	    private static HashMap<String, Integer> dicMap = new HashMap<String, Integer>();   
	    private static HashMap<Integer, Double> features = new HashMap<Integer, Double>(); 
	    private static boolean useLincense = true; //true   

	    public boolean Init_config(String modelName, String configfile, String dicfile) //throws Exception
	    {    	
	    	//load train model
	    	try {
	    		int treeCount = 0;
	        System.out.println("begin to load train model");
	    		LoadTrainModel(modelName);         	
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("load train model file failed...");
				return false;
			}
	    	System.out.println("load train model file OK...");
	    	
	    	//load train config file 
	    	try {
	        System.out.println("begin to load config file");
	    		LoadConfigFile(configfile);         	
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("load train config file failed...");
				return false;
			}
	    	System.out.println("load train config file OK...");
	    	
	    	//load dicitionary
	    	try {
	        System.out.println("begin to load dict file");
	    		LoadDicFile(dicfile);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("load dicitionary file failed...");
				return false;
			}
	    	System.out.println("load dicitionary file OK...");
	    	return true;    	
	    }
	         
	    private static void LoadConfigFile(String configName) throws IOException
	    {
	    	String line = "";
	    	String text = "";
	    	String[] array;
	    	
	    	BufferedReader reader = new BufferedReader(new FileReader(configName));
	 	    while ((line = reader.readLine()) != null)
	 	    {
	 	        //[tree_0]
	             //System.out.println(line);
	             text = line.trim();
	             array = text.split(" ");
	               
	             if (array[0].toLowerCase().equalsIgnoreCase("loss_type:"))
	             {  	 
	                //  return array[1].toUpperCase();
	            	if (array[1].toUpperCase().equalsIgnoreCase("LS")){            		
	            		task_type = "0";
	            	}
	            	else {
	            		task_type = "1";
					}           	 
	             }
	 	    }
	 	    reader.close();
	 	    
	 	   // System.out.println("task_type: " + task_type);
	 	    return; 
	    }
	    
	    /*ETL
	      name:feaname1,index:1
	      name:feaname2,index:2
	      name:feaname3,index:3
	      name:feaname4:string1,index:4
	      name:feaname4:string2,index:5
	      name:feaname4:string3,index:6 */
	    private static void LoadDicFile(String dicFile) throws IOException
	    {
	    	dicMap.clear();
	    	
	    	String line = "";
	    	String text = "";
	    	String[] array;
	    	String[] tmp;
	    	BufferedReader reader = new BufferedReader(new FileReader(dicFile));
	 	    while ((line = reader.readLine()) != null)
	 	    {
	 	         text = line.trim();
	             array = text.split(",");
	               
	             tmp = array[0].split(":");
	             if (tmp.length == 2)
	             {
	            	 dicMap.put(tmp[1], Integer.parseInt(array[1].split(":")[1]));            	 
	             }
	             else if (tmp.length == 3)
	             {
	            	 dicMap.put(tmp[1]+ ":" + tmp[2], Integer.parseInt(array[1].split(":")[1]));
				 }
	             else {
				     continue;	
				 }
	 	    }
	 	    reader.close();
	 	    
	 	/*   for (Entry<String, Integer> entry_sub : dicMap.entrySet())
	       {
	          System.out.println("name:" + entry_sub.getKey() + "," +  "index:" + entry_sub.getValue()); //                                                                
	       } */	    
	 	   return;   	
	    }   
	    
	    //
	    private static void LoadTrainModel(String modelName) throws IOException 
	    {
		    int tree_idx = 0; 
	        String line = null;
	        String[] array = null;
	        String[] content = null;
	        String[] leaf_array = null;
	        String[] condition_array = null;
	        String text = null;
	        String condition = null;
	        int start_index = -1;
	        int end_index = -1;

	        tree_node_vec.clear();
		    BufferedReader reader = new BufferedReader(new FileReader(modelName));
		    while ((line = reader.readLine()) != null)
		    {
		        //[tree_0]
	            //System.out.println(line);
	            text = line.trim();
	            
	            /* booster[0]:
	                  0:[170<45.5] yes=1,no=2,missing=1,gain=0.00874557
	                      1:[464<104.034] yes=3,no=4,missing=3,gain=0
	                          3:[264<9.5] yes=5,no=6,missing=5,gain=3.82713e-317
	                               5:leaf=0.0029847
	                               6:leaf=-0.000419277
	                          4:leaf=-0.000314065
	                      2:leaf=-0.000516956  */
	            
	            if (text.startsWith("booster"))  // booster[2]:
	            {
	                start_index = text.indexOf("[");
	                end_index = text.indexOf("]");
	                
	                tree_idx = Integer.valueOf(text.substring(start_index+1, end_index));
	                 
	                HashMap<Integer, node_t> node_hash = new HashMap<Integer, node_t>();
	                //tree_node_hash.put(tree_idx, node_hash);
	                
	                tree_node_vec.add(node_hash);
	                treeCount++;
	            }
	            //node_0=1,2,null,4,5316,0,,0
	            else if (text.indexOf("yes") != -1) //1:[102<13.5] yes=3,no=4,missing=3,gain=5.43472e-323
	            {
	                array = text.split(" ");
	                
	                start_index = array[0].indexOf("[");
	                end_index = array[0].indexOf("]"); 
	                
	                node_t tree_node = new node_t();
	                //
	                condition = array[0].substring(start_index+1, end_index);
	                
	                tree_node.node_id = Integer.valueOf(array[0].split(":")[0]); //
	                
	                if (condition.indexOf("<") != -1)
	                {
	                   condition_array = condition.split("<");                
	                }
	                else if (condition.indexOf("<=") != -1)
	                {
	                   condition_array = condition.split("<=");                    
	                }
	                else if (condition.indexOf(">") != -1)
	                {   
	                   condition_array = condition.split(">");                 
	                }
	                else if (condition.indexOf(">") != -1)
	                {
	                   condition_array = condition.split(">=");                 
	                }
	                else if (condition.indexOf("=") != -1)
	                {
	                   condition_array = condition.split("=");             
	                }
	                else
	                   continue;
	                
	                //
	                tree_node.feature_id = Integer.parseInt(condition_array[0]);
	                
	                //
	                tree_node.threshold = Double.valueOf(condition_array[1]);                 
	                                    
	                content = array[1].split(",");
	                for (String e : content)
	                {
	                    if (e.indexOf("yes") != -1) //
	                    {
	                        tree_node.left = Integer.valueOf(e.split("=")[1]); //                                               
	                    }
	                    else if (e.indexOf("no") != -1)  //
	                    {                   
	                        tree_node.right = Integer.valueOf(e.split("=")[1]); //right_id                       
	                    }
	                    else if (e.indexOf("missing") != -1) //
	                    {                   
	                        tree_node.miss = Integer.valueOf(e.split("=")[1]); // miss                        
	                    }
	                    else if (e.indexOf("gain") != -1) //                                
	                    {                   
	                         tree_node.gain = e.split("=")[1];   // gain                     
	                    }               
	                }           
	                
	                //
	                if (!(tree_node_vec.get(tree_idx).containsKey(tree_node.node_id)))
	                {
	                   tree_node_vec.get(tree_idx).put(tree_node.node_id, tree_node);
	                } 
	            }
	            else if (text.indexOf("leaf") != -1) //11:leaf=-0.00116664
	            {
	                    content = text.split("=");
	                
	                    node_t tree_node = new node_t();
	                    tree_node.node_id = Integer.valueOf(content[0].split(":")[0]); //
	                    tree_node.value = Double.valueOf(content[1]);            
	                
	                    //
	                    if (!(tree_node_vec.get(tree_idx).containsKey(tree_node.node_id)))
	                    {
	                        tree_node_vec.get(tree_idx).put(tree_node.node_id, tree_node);
	                    }               
	            }           
	        }
	        reader.close();
	                
	        //
	   /*     for(int i =0; i < tree_node_vec.size(); i++)
	        {
	             System.out.println("tree_" + i + " ");
	                                             
	             for (Entry<Integer, node_t> entry_sub : tree_node_vec.get(i).entrySet())
	             {
	                    //System.out.print("node_" + entry_sub.getKey() + " ");
	                    System.out.print("node_id " + entry_sub.getValue().node_id + " "); //
	                    System.out.print("feature_id " + entry_sub.getValue().feature_id + " ");
	                    System.out.print("threshold " + entry_sub.getValue().threshold + " "); 
	                    System.out.print("left " + entry_sub.getValue().left + " ");      // 
	                    System.out.print("right " + entry_sub.getValue().right + " ");    //
	                    System.out.print("miss " + entry_sub.getValue().miss + " ");      //miss
	                    System.out.print("gain " + entry_sub.getValue().gain + " ");      //gain  
	                    System.out.println("value " + entry_sub.getValue().value + " ");  //value(only leaf val)                                    
	             }
	        }   */										
		}
	    
	    public JsonObject predictData(String value) //throws Exception 
	    {
	        System.out.println("begin to predict data");
	        String[] array = value.trim().split("\\ |\\\t|\\\\x01", -1);	              
	        int feature_length = array.length; 
	        String[] tmp; 
	        int feature_id = 0; 
	        double feature_value = 0.0;
	        
	        /*ETL
	        name:张三,index:1
	        name:李四,index:2
	        name:王五,index:3
	        name:颜色:黄色,index:4
	        name:颜色:红色,index:5
	        name:颜色:绿色,index:6 */
	        
	     // feaname1:xxx feaname2:xx feaname3:xxxx  "张三:0.06 李四:0.09 颜色:红色"
	        features.clear();
	        for (int i=0; i< feature_length; i++)
	        {
	         //  tmp = array[i].split(":");
	           
	           if (dicMap.containsKey(array[i])) //
	           {
	        	   feature_id = dicMap.get(array[i]); 
	               feature_value = 1.0;      	   
	           }
	           else 
	           {
	        	   tmp = array[i].split(":");
	        	   
	        	   if (dicMap.containsKey(tmp[0])) //
	               {
	            	   feature_id = dicMap.get(tmp[0]); 
	                   feature_value = Double.parseDouble(tmp[1]);      	   
	               }
	        	   else {
	        		   continue;
	        	   }	
	           }          	   
			   
	          // System.out.println(feature_id + " : " + feature_value);
	           features.put(feature_id, feature_value);
	        } 
	        
	   /*     for (Entry<Integer, Double> entry_sub : features.entrySet())
	        {
	            System.out.println(entry_sub.getKey() + "," +  entry_sub.getValue()); //                                                                
	        }   */      

	        int leaf_index = 0;
	        int feature_index = 0;
	        // [10, 20, 30, 40, 50, 60]     
	        HashMap<Integer, node_t> node_map;
	        node_t tmp_node;
	        double v = 0;
	        double pctr_value = 0.0;
	        //double value1 = 0.0;
	        //int node_index = 0;
	        try{
	        	System.out.println("treeCount:"+treeCount);
	        for (int tree_index = 0; tree_index <treeCount; tree_index++)
	        {       
	            leaf_index = 0;
	            feature_index = 0;
	            //
	            System.out.println("tree_node_vec:"+tree_node_vec.size());
	            System.out.println("tree_index:"+tree_index);
	            node_map = tree_node_vec.get(tree_index);
	            
		            while ((node_map.get(leaf_index).left != -1) && (node_map.get(leaf_index).right != -1))
		            {
		                feature_index = node_map.get(leaf_index).feature_id;
	
			            v = 0;
		                if (features.containsKey(feature_index))
		                {
		                    v = Double.valueOf(features.get(feature_index));
				            if (v < node_map.get(leaf_index).threshold)
		                    {  
		                       leaf_index = node_map.get(leaf_index).left;
		                    }
		                    else
		                    {
		                       leaf_index = node_map.get(leaf_index).right;                           
		                    }
		                }		
		                else
			            {	
		                   	leaf_index = node_map.get(leaf_index).miss;			    
		                }    
		            }
	           
	            System.out.println("leaf_index");
		        pctr_value += node_map.get(leaf_index).value;
	        }
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        if (task_type.equalsIgnoreCase("1")) 
	        {
	            pctr_value = 1/(1 + Math.exp(-pctr_value));
	        }

	        //System.out.println("pctr_value: " + pctr_value);  
	        System.out.println("success to predict data");    
	        
	        JsonObject result= new JsonObject();
	        result.addProperty("status", "success");
	        result.addProperty("pctr_value", pctr_value);
	        return result;                        
	    }

	   public static void main(String[] args) //throws Exception
	   {
	      /* Init_config("D:\\test\\pdf\\conf\\model.txt", "D:\\test\\pdf\\conf\\config.yaml", "D:\\test\\pdf\\conf\\dict.txt");
			
	       double pctr_value = predict_data("张三:0.06 王五:0.09 颜色:绿色");
	       System.out.println(pctr_value);
		   
		   String str = "dca904944803"; 
		   System.out.println(DigestUtils.md5Hex(str));
		   System.out.println(DigestUtils.md5Hex(str));
		   
		   Random rand = new Random();
		   int min = 0;
		   int max = 100;
		   double result = 0.0;        */
	   }

	public JsonObject runPredict(AlgorithmParam algorithmParam) {
		Init_config("D:\\test\\pdf\\conf\\model.txt", "D:\\test\\pdf\\conf\\config.yaml", "D:\\test\\pdf\\conf\\dict.txt");
		return predictData(algorithmParam.getData());
	}

}
