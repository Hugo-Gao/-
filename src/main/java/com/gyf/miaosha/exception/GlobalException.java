package com.gyf.miaosha.exception;

import com.gyf.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException
{
    private static final long serialVersionUID = 6897257086856515900L;
    private CodeMsg codeMsg;
    public GlobalException(CodeMsg msg)
    {
        super(msg.toString());
        this.codeMsg = msg;
    }
    public CodeMsg getCodeMsg()
    {
        return codeMsg;
    }
}
