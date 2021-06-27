package com.penglab.hi5.basic.learning.filter;

import java.util.Arrays;

public class Gaussian {
    private double sigma;
    private double sigma2;
    private double norm;
    private int order;
    private double[] hermitePolynomial;

    public Gaussian(){
        double sigma = 1.0;
        int derivativeOrder = 0;
        this.sigma = sigma;
        sigma2 = -0.5/Math.pow(sigma,2);
        norm = 0;
        order = derivativeOrder;
        hermitePolynomial = new double[derivativeOrder/2 + 1];
        double sq = sigma*sigma;
        switch (order)
        {
            case 1:
            case 2:
                norm = -1/(Math.sqrt(2*Math.PI)*sq*sigma);
                break;
            case 3:
                norm = 1/(Math.sqrt(2*Math.PI)*sq*sq*sigma);
                break;
            default:
                norm = 1/(Math.sqrt(2*Math.PI)*sigma);
        }
        calculateHermitePolynomial();
    }
    public Gaussian(double sigma){
        int derivativeOrder = 0;
        this.sigma = sigma;
        sigma2 = -0.5/Math.pow(sigma,2);
        norm = 0;
        order = derivativeOrder;
        hermitePolynomial = new double[derivativeOrder/2 + 1];
        double sq = sigma*sigma;
        switch (order)
        {
            case 1:
            case 2:
                norm = -1/(Math.sqrt(2*Math.PI)*sq*sigma);
                break;
            case 3:
                norm = 1/(Math.sqrt(2*Math.PI)*sq*sq*sigma);
                break;
            default:
                norm = 1/(Math.sqrt(2*Math.PI)*sigma);
        }
        calculateHermitePolynomial();
    }
    public Gaussian(double sigma, int derivativeOrder){
        this.sigma = sigma;
        sigma2 = -0.5/Math.pow(sigma,2);
        norm = 0;
        order = derivativeOrder;
        hermitePolynomial = new double[derivativeOrder/2 + 1];
        double sq = sigma*sigma;
        switch (order)
        {
            case 1:
            case 2:
                norm = -1/(Math.sqrt(2*Math.PI)*sq*sigma);
                break;
            case 3:
                norm = 1/(Math.sqrt(2*Math.PI)*sq*sq*sigma);
                break;
            default:
                norm = 1/(Math.sqrt(2*Math.PI)*sigma);
        }
        calculateHermitePolynomial();
    }

    public double getSigma() {
        return sigma;
    }

    public int getOrder() {
        return order;
    }

    public double getRadius(double sigmaMultiple){
        return Math.ceil(sigma*(sigmaMultiple + 0.5*getOrder()));
    }

    public double gaussian(double x){
        double x2 = x*x;
        double g = norm*Math.exp(x2*sigma2);
        switch (order){
            case 0:
                return g;
            case 1:
                return x*g;
            case 2:
                return (1.0-(x/sigma)*(x/sigma))*g;
            case 3:
                return (3.0-(x/sigma)*(x/sigma))*x*g;
            default:
                return order%2==0?g*horner(x2):x*g*horner(x2);
        }
    }

    private double horner(double x){
        int i = order/2;
        double res = hermitePolynomial[i];
        for(--i; i>=0; --i){
            res = x*res + hermitePolynomial[i];
        }
        return res;
    }

    private void calculateHermitePolynomial(){
        if(order == 0){
            hermitePolynomial[0] = 1.0;
        }else if(order == 1){
            hermitePolynomial[0] = -1/(sigma*sigma);
        }else {
            double s2 = -1/(sigma*sigma);
            double[] hn = new double[3*order+3];
            Arrays.fill(hn, 0);

            int hn0 = 0, hn1 = hn0 + order +1, hn2 = hn1 + order + 1;
            hn[hn2] = 1.0;
            hn[hn1+1] = s2;

            for(int i=2; i<order; i++){
                hn[hn0] = s2*(i-1)*hn[hn2];
                for(int j=1; j<=i; j++){
                    hn[hn0+j] = s2*(hn[hn1+j-1]+(i-1)*hn[hn2+j]);
                }
                int ht = hn2;
                hn2 = hn1;
                hn1 = hn0;
                hn0 = ht;
            }

            for(int i=0; i<hermitePolynomial.length; i++){
                hermitePolynomial[i] = order%2==0?hn[hn1+2*i]:hn[hn1+2*i+1];
            }
        }
    }
}
