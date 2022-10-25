//TP3 - INTELIGENCIA ARTIFICIAL

import java.util.*;
import java.io.*;

class Red_Hopfield {

    static int M = 10;
    static int N = M*M;
    static int max_patrones = 3;
    static int[][] matrix_pesos = new int[N][N];
    static int[] entradas = new int[N];
    static int[] salidas = new int[N];
    
    // Se crea un vector de patrones
    static ArrayList<int[]> patrones = new ArrayList<int[]>();
    
    //Círculo con borde simple y punto central
    static int[] patron_1 = {
        0,  0,  0,  0,  0,  0,  0,  0,  0, 0,
        0,  0,  1,  1,  1,  1,  1,  0,  0, 0,
        0,  1,  0,  0,  0,  0,  0,  1,  0, 0,
        0,  1,  0,  0,  0,  0,  0,  0,  1, 0,
        0,  1,  0,  0,  1,  1,  0,  0,  1, 0,
        0,  1,  0,  0,  1,  1,  0,  0,  1, 0,
        0,  1,  0,  0,  0,  0,  0,  0,  1, 0,
        0,  0,  1,  0,  0,  0,  0,  1,  0, 0,
        0,  0,  0,  1,  1,  1,  1,  0,  0, 0,
        0,  0,  0,  0,  0,  0,  0,  0,  0, 0,
    };
    
    // Círculo con borde más definido y punto central
    static int[] patron_2 = {
        0,  0,  1,  1,  1,  1,  1,  1,  0,  0,
        0,  1,  1,  1,  1,  1,  1,  1,  0, 0,
        0,  1,  0,  0,  0,  0,  0,  1,  1, 1,
        1,  1,  0,  0,  0,  0,  0,  0,  1, 1,
        1,  1,  0,  0,  1,  1,  0,  0,  1, 1,
        1,  1,  0,  0,  1,  1,  0,  0,  1, 1,
        1,  1,  0,  0,  0,  0,  0,  0,  1, 1,
        0,  1,  1,  0,  0,  0,  0,  1,  1, 0,
        0,  0,  1,  1,  1,  1,  1,  1,  0, 0,
        0,  0,  0,  1,  1,  1,  1,  0,  0, 0,
    };
    
    //Círculo más pequeño con punto central
    static int[] patron_3 = {
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  0,  0, 0,
        0,  0,  1,  0,  0,  0,  0,  1,  0, 0,
        0,  0,  1,  0,  0,  0,  0,  1,  0, 0,
        0,  0,  1,  0,  1,  1,  0,  1,  0, 0,
        0,  0,  1,  0,  1,  1,  0,  1,  0, 0,
        0,  0,  1,  0,  0,  0,  0,  1,  0, 0,
        0,  0,  0,  1,  0,  0,  1,  0,  0, 0,
        0,  0,  0,  0,  1,  1,  0,  0,  0, 0,
        0,  0,  0,  0,  0,  0,  0,  0,  0, 0,
    };
    
    
    //Función de Activación
    public static int f_activacion (int x) {
        if (x > 0) return 1;
        else return -1;
    }
    
    
    public static void establecerNivelRuido (int ex, double p) {
        Random rand = new Random();
		int[] aux = patrones.get(ex);

        for (int i = 0; i < N; i++) {
			entradas[i] = aux[i];
            if (rand.nextDouble() < p) entradas[i] *= -1;
        }
        return;
    }
    
    
    public static void establecerPesos() {
        int i, j, k;
        
        //Primero limpiamos la matriz de pesos
        for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++){
                matrix_pesos[i][j] = 0;
            }
        }
        
        for (k = 0; k < max_patrones; k++) {
            for (i = 0; i < N; i++){
                for (j = 0; j < N; j++) {
                    
                    //Se verifica que no se generen conexiones a si mismas
                    if (i==j) continue;
                    
                    int[] u = patrones.get(k);
                    matrix_pesos[i][j] += (u[i]) * (u[j]);
                }
            }
        }
        
        return;
    }
    
    
    public static void determinarSalidas() {
        int i, j;
        int[][] aux = new int[N][N];
        
        for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
                if (i == j) continue;
                aux[i][j] += entradas[i] * matrix_pesos[i][j];
            }   
        }
        
        for (j = 0; j < N; j++) {
            salidas[j] = 0;
            for (i = 0; i < N; i++) {
                salidas[j] += aux[i][j];
            }
            salidas[j] = f_activacion(salidas[j]);
        }
        return;
    }
    
    //Se imprimen en pantalla los resultados
    public static void Resultados (int[] vector) {
        int i;
        
        for (i = 0; i < N; i++){
			//System.out.print( vector[i] );
            if ( (i % M) == 0) System.out.print("\n");

            if (vector[i] < 0) System.out.print(" ");

            else System.out.print("1 ");
        }

        System.out.print("\n");
    }
    
    
    public static void main (String[] args) {
        System.out.print("\nIniciando carga de patrones\n\n");
        patrones.add(patron_1); //Círculo con borde simple y punto central
        patrones.add(patron_2); // Círculo con borde más definido y punto central
        patrones.add(patron_3); //Círculo más pequeño con punto central
        
        int n, i;
        double ruido;
        
        establecerPesos();
        
        for (n = 0; n < max_patrones; n++) {
            
            ruido = 0.0; //Se establece un nivel de ruido
            
            for (i = 0; i < 4; i++) {
                System.out.print("\n--\nruido: " + ruido + "\n");
            
                establecerNivelRuido(n, ruido );
                Resultados(salidas);
               
                determinarSalidas();
                
                System.out.print("Resultado " + n + "\n");
                Resultados(salidas);
                
                ruido += 0.01;//Se aumenta en cada iteración
                
            }
            
            System.out.print("\n--------------FIN---------------\n\n");
            
        }
    }
}
