package tm_proyecto;

public class LZ77 {

    private int mEnt;
    private int mDes;
    private int eL;
    private int dL;
    
    public LZ77(int ent, int des) throws Exception {
        // ventana entrada es mas corta o igual que la deslizante y ambos son potencia de dos
        if(checkPow2(ent) && checkPow2(des)){
            if(ent <= des){
                    this.mEnt = ent;
                    this.mDes = des;
            }else{ // asumo que si estan en orden incorrecto es porque el usuario se ha equivocado
                    this.mDes = ent;
                    this.mEnt = des;
            }
            // longitud en bits de ambos numeros
            dL = 31 - Integer.numberOfLeadingZeros(mDes);
            eL = 31 - Integer.numberOfLeadingZeros(mEnt);
            System.out.println(dL+" "+eL);
            
            
        }else{
            throw new Exception("Alguna de las dos ventanas no es potencia de 2");
        }
    }
    
    private boolean checkPow2(int number){
        // 1 es tecnicamente potencia de 2, pero 0 no
        return (number > 0 && ((number & (number - 1)) == 0));
    }
    
    // Dada una serie la prepara para ser comprimida
    public  String findSecuenceErrors(String serie){
        int repeat = 1;
        // Si encontramos una repeticion de mDes-1 al final no creara conflicto, llegamos hasta length-1
        for(int i = 1; i<serie.length()-1;i++){
            // Caracter igual al al anterior
            if(serie.charAt(i-1) == serie.charAt(i))
                repeat++;
            // Caracter nuevo, primera aparicion
            else
                repeat=1;
            
            // Cuando encuentra una serie de longitud mDes-1 del mismo numero
            if(repeat==mDes-1){
                // Inserta el complementario al valor repetido inmediatamente despues
                // ('1'-'1'=0; '1'-'0'=1 asi conseguimos el contrario, char en ascii  '0'=48 y '1' = 49)
                serie = serie.substring(0, i+1) + ('1'-serie.charAt(i)) + serie.substring(i+1);
                repeat = 1;
            }
        }
        return serie;
    }
    
    // Dada una serie modificada, recrea la original
    public String recreateSecuence(String serie){
        int repeat = 1;
        // Si encontramos una repeticion de mDes-1 al final no creara conflicto, llegamos hasta length-1
        for(int i = 1; i<serie.length()-1;i++){
            // Caracter igual al al anterior
            if(serie.charAt(i-1) == serie.charAt(i))
                repeat++;
            // Caracter nuevo, primera aparicion
            else
                repeat=1;
            
            // Cuando encuentra una serie de longitud mDes-1 del mismo numero
            if(repeat==mDes-1){
                // Eliminamos el siguiente elemento, ya que es el insertado anteriormente
                serie = serie.substring(0, i+1) + serie.substring(i+2);
                repeat = 1;
                i++;
            }
        }
        return serie;
    }
    
    public String comprimirConFlag(String entrada) throws Exception{
        int remaining = entrada.length()-mDes;
        // si Mdes+Ment<= longitud datos a comprimir
        if(remaining >= mEnt){
            
            String result = entrada.substring(0, mDes);
            
            for(int i = 0; (i+mDes) < entrada.length();){
                
                String vDes = entrada.substring(i, Math.min(i+mDes,entrada.length()));
                String vEnt = entrada.substring(i+mDes, Math.min(i+mDes+mEnt,entrada.length()));
                String match = match(vDes, vEnt);
                
                if (!(match.equals(""))) {
                    result+="1";
                    result+=match;
                    // Si avanzamos mEnt, lo representamos con un 0, hay que tomar precauciones
                    int next = Integer.parseInt(match.substring(0, eL),2);
                    if(next==0)
                        next = this.mEnt;
                    i += next;
                } else {
                    result+="0";
                    result+=entrada.charAt(i+mDes);
                    i++;
                }
            }
            return result;
        }
        throw new Exception("Entrada mas pequeña que las ventanas propuestas");
    }
    
    public String descomprimirConFlag(String entrada){
        String result = entrada.substring(0, mDes);
        for(int i = mDes; i<entrada.length();){
            // found
            if(entrada.charAt(i)=='1'){
                i++;
                
                int L = Integer.parseInt(entrada.substring(i, i+eL),2);
                int D = Integer.parseInt(entrada.substring(i+eL, i+eL+dL),2);
                if(L==0)
                    L=this.mEnt;
                if(D==0)
                    D=this.mDes;
                result+= result.substring(result.length()-D, result.length()-D+L);
                i+=eL+dL;
            // wasnt found
            }else if(entrada.charAt(i)=='0'){
                i++;
                result+=entrada.charAt(i);
                i++;
            }            
        }
        return result;
    }
    
    
    public String comprimirSinFlag(String entrada) throws Exception{
        // Editamos la entrada para ser comprimida
        entrada = this.findSecuenceErrors(entrada);
        int remaining = entrada.length()-mDes;
        // si Mdes+Ment<= longitud datos a comprimir
        if(remaining >= mEnt){
            
            String result = entrada.substring(0, mDes);
            for(int i = 0; (i+mDes) < entrada.length();){
                
                String vDes = entrada.substring(i, Math.min(i+mDes,entrada.length()));
                String vEnt = entrada.substring(i+mDes, Math.min(i+mDes+mEnt,entrada.length()));
                String match = match(vDes, vEnt);
                
                // Siempre hay match
                
                result+=match;
                // Si avanzamos mEnt, lo representamos con un 0, hay que tomar precauciones
                int next = Integer.parseInt(match.substring(0, eL),2);
                if(next==0)
                    next = this.mEnt;
                i += next;
            }
            return result;
        }
        throw new Exception("Entrada mas pequeña que las ventanas propuestas");
    }
    
    public String descomprimirSinFlag(String entrada){
        String result = entrada.substring(0, mDes);
        for(int i = mDes; i<entrada.length();){
            
            int L = Integer.parseInt(entrada.substring(i, i+eL),2);
            int D = Integer.parseInt(entrada.substring(i+eL, i+eL+dL),2);
            if(L==0)
                L=this.mEnt;
            if(D==0)
                D=this.mDes;
            result+= result.substring(result.length()-D, result.length()-D+L);
            i+=eL+dL;        
        }
        // Eliminamos las modificaciones realizadas al comprimir
        result=this.recreateSecuence(result);
        return result;
    }
    
    private String match(String des, String ent){
        // Empezamos con toda la ventana de entrada y la vamos acortando cada iteracion
        for(int i = ent.length(); i>0; i--){
            int position = mDes-des.indexOf(ent.substring(0, i));
            
            // si el index es diferente de -1 es que lo ha encontrado y la resta pasa la siguiente condicion
            if(!(position>mDes)){
                
                 // si empieza al principio de la cadena, lo representamos con valor 0
                if(position == mDes)
                    position = 0;
                if(i == mEnt)
                    i=0;
                String E =  Integer.toBinaryString(mEnt | i).substring(1);
                String D =  Integer.toBinaryString(mDes | position).substring(1);
                return E+D;
            }
        }
        return "";
    }
    
}
