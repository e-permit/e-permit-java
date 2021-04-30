package epermit.common;


public enum PermitType {
    BILITERAL(1), TRANSIT(2), THIRDCOUNTRY(3);

    private Integer code;

    private PermitType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getStringCode(){
        String result ="";
        switch(this){
            case BILITERAL: 
              result = "B";
              break;
            case TRANSIT:
              result = "T";
              break;
            case THIRDCOUNTRY:
              result = "3RD";
              break; 
        }
        return result;
    }
}
