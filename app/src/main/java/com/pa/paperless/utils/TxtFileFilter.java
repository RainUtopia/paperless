package com.pa.paperless.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24.
 *  txt文档-文件过滤器
 */

public class TxtFileFilter implements FilenameFilter{
    private List<String> types;

    public TxtFileFilter() {
        this.types = new ArrayList<>();
    }

    public TxtFileFilter(List<String> type) {
        super();
        this.types = type;
    }

    @Override
    public boolean accept(File file, String filename) {

        for(Iterator<String> iterator = types.iterator(); iterator.hasNext();){
            String type = (String) iterator.next();
            if(filename.endsWith(type)){
                return  true;
            }
        }
        return false;
    }
    public void addType(String type){
        types.add(type);
    }
}
