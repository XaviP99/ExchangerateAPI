package com.example.helloworld;

import com.sun.source.tree.ReturnTree;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiOperation(value = "/ExchangeRateAPI")
@RestController
public class controller {
    public int FlagA=0, FlagB=0, FlagC=0,FlagD=0;                       // Make sure that  each cache is on clean after being used


    //alinea A
    @Cacheable(value="alineaA")
    @ApiOperation(value="Get exchange rate from Currency A to Currency B")
    @ResponseBody
    @GetMapping("/alineaA/{from}/{to}")
    public exchangerate alineaA (@PathVariable("from") String from, @PathVariable("to") String to){
        FlagA=1;
        RestTemplate restTemplate=new RestTemplate();
        String url = "https://api.exchangerate.host/convert?from="+from+"&to="+to ;
        exchangerate result = restTemplate.getForObject(url,exchangerate.class);
        System.out.print("aqui");                                          // How to test that  the data in swagger is given by the cache memory

        return result;

    }

    //alinea B
    @Cacheable(value="alineaB")
    @ApiOperation(value="Get all exchange rates from Currency A")
    @ResponseBody
    @GetMapping("/alineaB/{base}")
    public   String alinea (@PathVariable("base")String base){
        FlagB=1;
        RestTemplate restTemplate = new RestTemplate();
        String url ="https://api.exchangerate.host/2020-04-04?base="+base;
        String result= restTemplate.getForObject(url,String.class);
        System.out.print("aqui");                               // How to test that the data in swagger is given by the cache memory

        return result;
    }
    // alinea C
    @Cacheable(value="alineaC")
    @ApiOperation(value="Get value conversion from Currency A to Currency B")
    @ResponseBody
    @GetMapping("/alineaC/{from}/{to}/{convertion_value}")
    public Double exch(@PathVariable("from")String from,@PathVariable("to") String to,@PathVariable("convertion_value") String value){
        FlagC=1;
        RestTemplate restTemplate= new RestTemplate();
        String url= "https://api.exchangerate.host/convert?from="+from+"&to="+to    ;
        exchangerate exchange= restTemplate.getForObject(url, exchangerate.class);
        double convertion= exchange.getResult() *  Integer.valueOf(value);   // Value of convertion, get the resut using the getResult() method and multiplies bye the value that the user want to convert
        System.out.print("aqui");                                            // How to test that that the data in swagger is given by the cache memory
        return convertion   ;
    }

    // alinea D
    @Cacheable(value="alineaD")
    @ApiOperation(value="Get value conversion from Currency A to a list of supplied currencies")
    @ResponseBody
    @GetMapping("/alineaD/{from}/{List of currencies}/{convertion_value}")
    public ArrayList<String> all (@PathVariable("from") String from,@PathVariable("List of currencies") ArrayList<String> list ,@PathVariable("convertion_value") String value){
        FlagD=1;
        RestTemplate restTemplate= new RestTemplate();
        String url;
        ArrayList<String> lista = new ArrayList<String>();                                          // Create an array than does not need to set the dimention
        String convertion;
                    for (int i = 0; i < list.size(); i++){                                          // Get all values one by one of the list for the convertion
                        url= "https://api.exchangerate.host/convert?from="+from+"&to="+list.get(i);
                        exchangerate result = restTemplate.getForObject(url,exchangerate.class);
                        convertion = String.valueOf(result.getResult() * Integer.valueOf(value)) ;  //Value of convertion, get the resut using the getResult() method and multiplies bye the value that the user want to convert
                        lista.add(convertion);                                                      // add each value to an array
                    }
        System.out.print("aqui");                                                                   // How to test that the data in swagger is given by the cache memory

        return lista;
    }

    //---------------------------- Clean CACHE ------------------------------//

            @Scheduled(fixedRate = 10000)// (1000-> 1 segundo) execute task 4 in 4 minutes
            public void evictAllcachesAtIntervals() {
                if(FlagA==1){
                    evictAllCacheValues("alineaA");
                }
                 else if(FlagB==1){
                     evictAllCacheValues("alineaB");
                 }
                else if(FlagC==1){
                    evictAllCacheValues("alineaC");
                }
                else if (FlagD==1){
                     evictAllCacheValues("alineaD");
                 }
            }
                    @Autowired                                                  // Make the connection to CacheManager.class
                    CacheManager cacheManager;
                    public void evictAllCacheValues(String cacheName) {
                        cacheManager.getCache(cacheName).clear();
                        if(FlagA==1){
                            FlagA=0;
                            System.out.print("eleminuAA");                      // Test Method to delete the cache
                        }
                        else if(FlagB==1){
                           FlagB=0;
                            System.out.print("eleminuBBB");
                        }
                        else if(FlagC==1){
                            FlagC=0;
                            System.out.print("eleminuCCC");
                        }
                        else if (FlagD==1){
                            FlagD=0;
                            System.out.print("eleminuDDD");
                        }
                    }
}
