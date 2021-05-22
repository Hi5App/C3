package com.penglab.hi5.basic.learning.filter;

import java.util.Arrays;
import java.util.Vector;

public class MultiConvolution {
    public static void internalSeparableConvolveMultiArray(int[] src, int[] srcSz, int[] mask,
                                                         int[] dest, Vector<Kernel1D> kernels){
        //only operate on first dimension here
        MultiArrayNavigator sNav = new MultiArrayNavigator(src,srcSz,0);
        MultiArrayNavigator tNav = new MultiArrayNavigator(dest,srcSz,0);
        MultiArrayNavigator maskNav = new MultiArrayNavigator(mask,srcSz,0);
        int[] srcTmpLine = sNav.getLine();
        int[] maskTmpLine = maskNav.getLine();
        int[] destTmpLine = new int[srcTmpLine.length];
        SeparableConvolution.convolveLine(srcTmpLine,maskTmpLine,destTmpLine,kernels.get(0));
        tNav.setLine(destTmpLine);
        while (sNav.next()){
            maskNav.next();
            tNav.next();
            srcTmpLine = sNav.getLine();
            maskTmpLine = maskNav.getLine();
            SeparableConvolution.convolveLine(srcTmpLine,maskTmpLine,destTmpLine,kernels.get(0));
            tNav.setLine(destTmpLine);
        }

        //operator on further dimensions
        for(int d=1; d<srcSz.length; d++){
            MultiArrayNavigator tNavD = new MultiArrayNavigator(dest,srcSz,d);
            MultiArrayNavigator maskNavD = new MultiArrayNavigator(mask,srcSz,d);
            int[] tmpLine = tNavD.getLine();
            int[] maskTmpLineD = maskNavD.getLine();
            int[] destTmpLineD = new int[tmpLine.length];
            SeparableConvolution.convolveLine(tmpLine,maskTmpLineD,destTmpLineD,kernels.get(d));
            tNavD.setLine(destTmpLineD);
            while (tNavD.next()){
                maskNavD.next();
                tmpLine = tNavD.getLine();
                maskTmpLineD = maskNavD.getLine();
                SeparableConvolution.convolveLine(tmpLine,maskTmpLineD,destTmpLineD,kernels.get(d));
                tNavD.setLine(destTmpLineD);
            }
        }
    }

    public static void gaussianSmoothMultiArray(int[] src, int[] srcSz, int[] mask,
                                                int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in gaussianSmoothMultiArray-----------------");
        Vector<Kernel1D> kernels = new Vector<>();
        int N = srcSz.length;
        opt.reset();
        for(int dim=0; dim<N; dim++){
            Kernel1D kernel = new Kernel1D();
            kernel.initGaussian(opt.sigmaScaled(),1.0,opt.getWindowRatio());
            kernels.add(kernel);
            opt.next();
        }
        internalSeparableConvolveMultiArray(src,srcSz,mask,dest,kernels);
    }

    public static void gaussianGradientMultiArray(int[] src, int[] srcSz, int[] mask,
                                                  int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in gaussianGradientMultiArray-----------------");
        Vector<Kernel1D> plainKernels = new Vector<>();
        int N = srcSz.length;
        for(int k=0; k<N; k++){
            if(srcSz[k] <= 0){
                System.out.println("channel is error");
            }
        }
        opt.reset();
        for(int dim=0; dim<N; dim++){
            Kernel1D kernel = new Kernel1D();
            kernel.initGaussian(opt.sigmaScaled(),1.0,opt.getWindowRatio());
            plainKernels.add(kernel);
            opt.next();
        }
        opt.reset();
        for(int dim=0; dim<N; dim++){
            Vector<Kernel1D> kernels = new Vector<>();
            for(int i=0; i<plainKernels.size(); i++){
                kernels.add(new Kernel1D(plainKernels.get(i)));
            }
            kernels.get(dim).initGaussianDerivative(opt.sigmaScaled(),1,1.0,opt.getWindowRatio());
            kernels.get(dim).scaleKernel(1.0/opt.stepSize());
            internalSeparableConvolveMultiArray(src,srcSz,mask,dest,kernels);
            opt.next();
        }
    }

    public static void gaussianGradientMagnitude(int[] src, int[] srcSz, int[] mask,
                                                 int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in gaussianGradientMagnitude-----------------");
        Arrays.fill(dest,0);
        int N = srcSz.length - 1;
        int[] grad = new int[dest.length];

        int[] sz = new int[srcSz.length-1];
        for(int i=0; i<srcSz.length-1; i++){
            sz[i] = srcSz[i];
        }
        for(int k=0; k<srcSz[N]; k++){
            gaussianGradientMultiArray(bindOuter(src,srcSz,k),bindOuter(mask,srcSz,k),sz,grad,opt);
            for(int i=0; i<dest.length; i++){
                dest[i] += grad[i]*grad[i];
            }
        }
        for(int i=0; i<dest.length; i++){
            dest[i] = (int) Math.sqrt(dest[i]);
        }

    }

    public static int[] bindOuter(int[] src, int[] srcSz, int k){
        int size = 1;
        for(int i=0; i<srcSz.length-1; i++){
            size *= srcSz[i];
        }
        int[] out = new int[size];
        System.arraycopy(src, k * size + 0, out, 0, size);
        return out;
    }

    public static void setBindOuter(int[] dest, int[] destSz, int[] out, int k){
        int size = 1;
        for(int i=0; i<destSz.length-1; i++){
            size *= destSz[i];
        }
        System.arraycopy(out, 0, dest, k * size + 0, size);
    }

    public static void laplacianOfGaussianMultiArray(int[] src, int[] srcSz, int[] mask,
                                                      int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in laplacianOfGaussianMultiArray-----------------");
        Vector<Kernel1D> plainKernels = new Vector<>();
        int N = srcSz.length;
        for(int k=0; k<N; k++){
            if(srcSz[k] <= 0){
                System.out.println("channel is error");
            }
        }
        opt.reset();
        for(int dim=0; dim<N; dim++){
            Kernel1D kernel = new Kernel1D();
            kernel.initGaussian(opt.sigmaScaled(),1.0,opt.getWindowRatio());
            plainKernels.add(kernel);
            opt.next();
        }
        opt.reset();
        int[] derivative = new int[dest.length];
        for(int dim=0; dim<N; dim++){
            Vector<Kernel1D> kernels = new Vector<>();
            for(int i=0; i<plainKernels.size(); i++){
                kernels.add(new Kernel1D(plainKernels.get(i)));
            }
            kernels.get(dim).initGaussianDerivative(opt.sigmaScaled(),2,1.0,opt.getWindowRatio());
            kernels.get(dim).scaleKernel(1.0/opt.stepSize());
            if(dim == 0){
                internalSeparableConvolveMultiArray(src,srcSz,mask,dest,kernels);
            }else {
                internalSeparableConvolveMultiArray(src,srcSz,mask,derivative,kernels);
                for(int i=0; i<dest.length; i++){
                    dest[i] += derivative[i];
                }
            }
            opt.next();
        }
    }

    public static void hessianOfGaussianMultiArray(int[] src, int[] srcSz, int[] mask,
                                                     int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in hessianOfGaussianMultiArray-----------------");
        Vector<Kernel1D> plainKernels = new Vector<>();
        int N = srcSz.length;
        int M = N*(N+1)/2;

        int[] sz = new int[srcSz.length+1];
        for(int i=0; i<srcSz.length; i++){
            sz[i] = srcSz[i];
        }
        sz[srcSz.length] = M;

        for(int k=0; k<N; k++){
            if(srcSz[k] <= 0){
                System.out.println("channel is error");
            }
        }
        opt.reset();
        for(int dim=0; dim<N; dim++){
            Kernel1D kernel = new Kernel1D();
            kernel.initGaussian(opt.sigmaScaled(),1.0,opt.getWindowRatio());
            plainKernels.add(kernel);
            opt.next();
        }
        opt.reset();

        ConvolutionOptions paramsI = new ConvolutionOptions(opt);
        for(int b=0, i=0; i<N; ++i){
            ConvolutionOptions paramsJ = new ConvolutionOptions(paramsI);
            for(int j=i; j<N; ++j, ++b){
                Vector<Kernel1D> kernels = new Vector<>();
                for(int k=0; k<plainKernels.size(); k++){
                    kernels.add(new Kernel1D(plainKernels.get(k)));
                }
                if(i==j){
                    kernels.get(i).initGaussianDerivative(paramsI.sigmaScaled(),2,1.0,paramsI.getWindowRatio());
                }else {
                    kernels.get(i).initGaussianDerivative(paramsI.sigmaScaled(),1,1.0,paramsI.getWindowRatio());
                    kernels.get(i).initGaussianDerivative(paramsJ.sigmaScaled(),1,1.0,paramsJ.getWindowRatio());
                }
                kernels.get(i).scaleKernel(1.0/paramsI.stepSize());
                kernels.get(j).scaleKernel(1.0/paramsJ.stepSize());
                int[] destTmp = bindOuter(dest,sz,b);
                internalSeparableConvolveMultiArray(src,srcSz,mask,destTmp,kernels);
                setBindOuter(dest,sz,destTmp,b);
                paramsJ.next();
            }
            paramsI.next();
        }
    }

    public static void structureTensorMultiArray(int[] src, int[] srcSz, int[] mask,
                                                   int[] dest, ConvolutionOptions opt){
        System.out.println("-------------in structureTensorMultiArray-----------------");
        Vector<Kernel1D> plainKernels = new Vector<>();
        int N = srcSz.length;
        int M = N*(N+1)/2;

        for(int k=0; k<N; k++){
            if(srcSz[k] <= 0){
                System.out.println("channel is error");
            }
        }
        opt.reset();

        int[] gradient = new int[dest.length];
        gaussianGradientMultiArray(src,srcSz,mask,gradient,opt);

        gaussianSmoothMultiArray(gradient,srcSz,mask,dest,opt);

    }

}
