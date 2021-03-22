package softnet.asn;

class Int64Encoder implements ElementEncoder
{
	private long m_Value;
	
	public Int64Encoder(long value)
	{
		m_Value = value;
	}
	
	public int estimateTLV()
	{
        if (m_Value > -128L)
        {
            if (m_Value <= 127L)
            {
                return 3;
            }
            else if (m_Value <= 32767L)
            {
                return 4;
            }
            else if (m_Value <= 8388607L)
            {
                return 5;
            }
            else if (m_Value <= 2147483647L)
            {
                return 6;
            }
            else if (m_Value <= 549755813887L)
            {
                return 7;
            }
            else if (m_Value <= 140737488355327L)
            {
                return 8;
            }
            else if (m_Value <= 36028797018963967L)
            {
                return 9;
            }
            else
                return 10;
        }
        else if (m_Value >= -32768)
        {
            return 4;
        }
        else if (m_Value >= -8388608)
        {
            return 5;
        }
        else if (m_Value >= -2147483648L)
        {
            return 6;
        }
        else if (m_Value >= -549755813888L)
        {
            return 7;
        }
        else if (m_Value >= -140737488355328L)
        {
            return 8;
        }
        else if (m_Value >= -36028797018963967L)
        {
            return 9;
        }
        else
            return 10;	
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		int LV_length = encodeLV(binStack);
		binStack.stack(UniversalTag.Integer);
        return 1 + LV_length;
	}
	
	private int encodeLV(BinaryStack binStack)
    {
    	byte b0 = (byte) (m_Value & 0x00000000000000ffL);
    	byte b1 = (byte) ((m_Value & 0x000000000000ff00L) >> 8);
    	byte b2 = (byte) ((m_Value & 0x0000000000ff0000L) >> 16);
    	byte b3 = (byte) ((m_Value & 0x00000000ff000000L) >> 24);
    	byte b4 = (byte) ((m_Value & 0x000000ff00000000L) >> 32);
    	byte b5 = (byte) ((m_Value & 0x0000ff0000000000L) >> 40);
    	byte b6 = (byte) ((m_Value & 0x00ff000000000000L) >> 48);
    	byte b7 = (byte) ((m_Value & 0xff00000000000000L) >> 56);
    	
    	if(b7 == 0)
    	{
    		if(b6 == 0)
    		{
	    		if(b5 == 0)
	    		{
		    		if(b4 == 0)
		    		{
				    	if(b3 == 0)
				    	{
				    		if(b2 == 0)
				    		{
				    			if(b1 == 0)
				    			{
				    				if(b0 == 0)
				    				{
					    				binStack.stack((byte)0);
					    	            binStack.stack((byte)1);	
					    	            return 2;
				    				}
				    				else if(b0 > 0)
				    				{
				    					binStack.stack(b0);
					    	            binStack.stack((byte)1);	
					    	            return 2;
				    				}
				    				else
				    				{
				    					binStack.stack(b0);
					    	            binStack.stack((byte)0);	
					    	            binStack.stack((byte)2);	
					    	            return 3;    					
				    				}
				    			}
				    			else if (b1 > 0)
				    			{
				    				binStack.stack(b0);
				    				binStack.stack(b1);
				    	            binStack.stack((byte)2);	
				    	            return 3;    				
				    			}
				    			else
				    			{
				    				binStack.stack(b0);
				    				binStack.stack(b1);
				    				binStack.stack((byte)0);
				    	            binStack.stack((byte)3);	
				    	            return 4;    				
				    			}
				    		}
				    		else if(b2 > 0)
				    		{
				    			binStack.stack(b0);
								binStack.stack(b1);
								binStack.stack(b2);
					            binStack.stack((byte)3);	
					            return 4;   
				    		}
				    		else
				    		{
				    			binStack.stack(b0);
								binStack.stack(b1);
								binStack.stack(b2);
								binStack.stack((byte)0);
					            binStack.stack((byte)4);	
					            return 5;   
				    		}    		
				    	}
				    	else if(b3 > 0)
				    	{
				    		binStack.stack(b0);
							binStack.stack(b1);
							binStack.stack(b2);
							binStack.stack(b3);
				            binStack.stack((byte)4);	
				            return 5;  
				    	}
				    	else
				    	{
				    		binStack.stack(b0);
							binStack.stack(b1);
							binStack.stack(b2);
							binStack.stack(b3);
							binStack.stack((byte)0);
				            binStack.stack((byte)5);	
				            return 6;  
				    	}
		    		}
		    		else if(b4 > 0)
		    		{
		    			binStack.stack(b0);
						binStack.stack(b1);
						binStack.stack(b2);
						binStack.stack(b3);
						binStack.stack(b4);
			            binStack.stack((byte)5);	
			            return 6;  
		    		}
		    		else
		    		{
		    			binStack.stack(b0);
						binStack.stack(b1);
						binStack.stack(b2);
						binStack.stack(b3);
						binStack.stack(b4);
						binStack.stack((byte)0);
			            binStack.stack((byte)6);	
			            return 7;  
		    		}
	    		}
	    		else if (b5 > 0)
	    		{
	    			binStack.stack(b0);
					binStack.stack(b1);
					binStack.stack(b2);
					binStack.stack(b3);
					binStack.stack(b4);
					binStack.stack(b5);
		            binStack.stack((byte)6);	
		            return 7;  
	    		}
	    		else 
	    		{
	    			binStack.stack(b0);
					binStack.stack(b1);
					binStack.stack(b2);
					binStack.stack(b3);
					binStack.stack(b4);
					binStack.stack(b5);
					binStack.stack((byte)0);
		            binStack.stack((byte)7);	
		            return 8;  	    			
	    		}
    		}
    		else if(b6 > 0)
    		{
    			binStack.stack(b0);
				binStack.stack(b1);
				binStack.stack(b2);
				binStack.stack(b3);
				binStack.stack(b4);
				binStack.stack(b5);
				binStack.stack(b6);
	            binStack.stack((byte)7);	
	            return 8;  
    		}
    		else
    		{
    			binStack.stack(b0);
				binStack.stack(b1);
				binStack.stack(b2);
				binStack.stack(b3);
				binStack.stack(b4);
				binStack.stack(b5);
				binStack.stack(b6);
				binStack.stack((byte)0);
	            binStack.stack((byte)8);	
	            return 9;
    		}
    	}
    	else if(b7 > 0)
    	{
    		binStack.stack(b0);
			binStack.stack(b1);
			binStack.stack(b2);
			binStack.stack(b3);
			binStack.stack(b4);
			binStack.stack(b5);
			binStack.stack(b6);
			binStack.stack(b7);
            binStack.stack((byte)8);	
            return 9;
    	}
    	else if(b7 == -1)
    	{
    		if(b6 == -1)
			{
				if(b5 == -1)
				{
					if(b4 == -1)
					{
						if(b3 == -1)
						{
							if(b2 == -1)
							{
								if(b1 == -1)
								{
									if(b0 < 0)
									{
										binStack.stack(b0);
							            binStack.stack((byte)1);	
							            return 2;
									}
									else
									{
										binStack.stack(b0);
							            binStack.stack((byte)-1);	
							            binStack.stack((byte)2);	
							            return 3;
									}
								}
								else if(b1 < 0)
								{
									binStack.stack(b0);
									binStack.stack(b1);
						            binStack.stack((byte)2);	
						            return 3;  	
								}
								else
								{
									binStack.stack(b0);
									binStack.stack(b1);
									binStack.stack((byte)-1);
						            binStack.stack((byte)3);	
						            return 4;
								}
							}
							else if(b2 < 0)
							{
								binStack.stack(b0);
								binStack.stack(b1);
								binStack.stack(b2);
					            binStack.stack((byte)3);	
					            return 4;  			
							}
							else
							{
								binStack.stack(b0);
								binStack.stack(b1);
								binStack.stack(b2);
					            binStack.stack((byte)-1);	
					            binStack.stack((byte)4);	
					            return 5;  			
							}
						}
						else if(b3 < 0)
						{
							binStack.stack(b0);
							binStack.stack(b1);
							binStack.stack(b2);
							binStack.stack(b3);
				            binStack.stack((byte)4);	
				            return 5;  			
						}
						else
						{
							binStack.stack(b0);
							binStack.stack(b1);
							binStack.stack(b2);
							binStack.stack(b3);
				            binStack.stack((byte)-1);	
				            binStack.stack((byte)5);	
				            return 6;  			
						}
					}
					else if(b4 < 0)
					{
						binStack.stack(b0);
						binStack.stack(b1);
						binStack.stack(b2);
						binStack.stack(b3);
						binStack.stack(b4);
			            binStack.stack((byte)5);	
			            return 6;  			
					}
					else
					{
						binStack.stack(b0);
						binStack.stack(b1);
						binStack.stack(b2);
						binStack.stack(b3);
						binStack.stack(b4);
			            binStack.stack((byte)-1);	
			            binStack.stack((byte)6);	
			            return 7;  			
					}
				}
				else if(b5 < 0)
				{
					binStack.stack(b0);
					binStack.stack(b1);
					binStack.stack(b2);
					binStack.stack(b3);
					binStack.stack(b4);
					binStack.stack(b5);
		            binStack.stack((byte)6);	
		            return 7;  			
				}
				else
				{
					binStack.stack(b0);
					binStack.stack(b1);
					binStack.stack(b2);
					binStack.stack(b3);
					binStack.stack(b4);
					binStack.stack(b5);
		            binStack.stack((byte)-1);	
		            binStack.stack((byte)7);	
		            return 8;  			
				}
			}
			else if(b6 < 0)
			{
				binStack.stack(b0);
				binStack.stack(b1);
				binStack.stack(b2);
				binStack.stack(b3);
				binStack.stack(b4);
				binStack.stack(b5);
				binStack.stack(b6);
	            binStack.stack((byte)7);	
	            return 8;  			
			}
			else
			{
				binStack.stack(b0);
				binStack.stack(b1);
				binStack.stack(b2);
				binStack.stack(b3);
				binStack.stack(b4);
				binStack.stack(b5);
				binStack.stack(b6);
	            binStack.stack((byte)-1);	
	            binStack.stack((byte)8);	
	            return 9;  			
			}
    	}
    	else
    	{
    		binStack.stack(b0);
			binStack.stack(b1);
			binStack.stack(b2);
			binStack.stack(b3);
			binStack.stack(b4);
			binStack.stack(b5);
			binStack.stack(b6);
			binStack.stack(b7);
            binStack.stack((byte)8);	
            return 9;
    	}
    }
	
}