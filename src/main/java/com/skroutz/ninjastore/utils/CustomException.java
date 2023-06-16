package com.skroutz.ninjastore.utils;

// class represents user-defined exception  
public class CustomException extends Exception  
{  
    public CustomException(String str)  
    {  
        // Calling constructor of parent Exception  
        super(str);  
    }  
}