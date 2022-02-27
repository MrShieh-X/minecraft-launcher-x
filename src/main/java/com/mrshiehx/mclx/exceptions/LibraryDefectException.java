package com.mrshiehx.mclx.exceptions;

import com.mrshiehx.mclx.bean.Library;

import java.util.List;

public class LibraryDefectException extends LaunchException{
    public final List<Library>list;
    public LibraryDefectException(List<Library>list) {
        super(String.format(list.size()==1?"the library file is not found: %s":"library files below are not found:\n%s",toS(list)));
        this.list=list;
    }

    private static String toS(List<Library> list) {
        if(list.size()==1){
            return (list.get(0).libraryJSONObject().optString("name"));
        }
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<list.size();i++){
            Library library=list.get(i);
            stringBuilder.append("       ").append(library.libraryJSONObject().optString("name"));
            if(i+1!=list.size()){
                stringBuilder.append('\n');
            }
        }
        return stringBuilder.toString();
    }
}
