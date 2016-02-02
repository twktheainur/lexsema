package org.getalp.lexsema.util.distribution;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public final class SparkSingleton {
    private SparkSingleton(){

    }

    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static JavaSparkContext javaSparkContext;

    public static void initialize(String sparkMaster, String appName){
        @SuppressWarnings("LocalVariableOfConcreteClass") final SparkConf sparkConf = new SparkConf().setAppName(appName);
        //noinspection LawOfDemeter
        javaSparkContext = new JavaSparkContext(sparkConf.setMaster(sparkMaster));
    }

    @SuppressWarnings("MethodReturnOfConcreteClass")
    public static JavaSparkContext getSparkContext(){
        //noinspection StaticVariableUsedBeforeInitialization
        return javaSparkContext;
    }


}
