package com.penglab.hi5.basic.learning.feature_select;

public class GaussianBlur {
    private enum Type{D1,D2,D3}

    protected int radius;
    protected Kernel kernel;
    private Type d;

    public GaussianBlur(int radius, float sigma, int type){
        if(type == 1){
            this.d = Type.D1;
        }else if(type == 2){
            this.d = Type.D2;
        }else if(type == 3){
            this.d = Type.D3;
        }else{
            this.d = Type.D3;
            System.out.println("other type is not supported!");
        }
        setRadius(radius,sigma);
    }

    public void setRadius(int radius, float sigma) {
        this.radius = radius;
        kernel = makeKernel(radius, sigma);
    }

    public static void convolveAndTranspose(Kernel kernel, int[] inPixels, int[] mask, int[] outPixels,
                                     int weight, int height, int depth, int edgeAction){
        float[] matrix = kernel.getKernel();
        int kernelW = kernel.getWeight();
        int kernelH = kernel.getHeight();
        int kernelD = kernel.getDepth();

        for(int k=0; k<depth; k++){
            for(int j=0; j<height; j++){
                for(int i=0; i<weight; i++){
                    int index = k*height*weight +j*weight +i;

                    if(mask[index] == 0){
                        outPixels[index] = inPixels[index];
                        continue;
                    }

                    float intensity = 0;
                    for(int kk=-kernelD/2; kk<kernelD/2; kk++){
                        for(int jj=-kernelH/2; jj<kernelH/2; jj++){
                            for(int ii=-kernelW/2; ii<kernelW/2; ii++){
                                int iz = k + kk;
                                int iy = j + jj;
                                int ix = i + ii;
                                if(edgeAction == 0){
                                    if(ix<0) ix = 0;
                                    if(iy<0) iy = 0;
                                    if(iz<0) iz = 0;
                                    if(ix>=weight) ix = weight - 1;
                                    if(iy>=height) iy = height - 1;
                                    if(iz>=depth) iz = depth - 1;
                                }else if(edgeAction == 1){
                                    if(ix<0) ix = (ix+weight)%weight;
                                    if(iy<0) iy = (iy+height)%height;
                                    if(iz<0) iz = (iz+depth)%depth;
                                    if(ix>=weight) ix = (ix+weight)%weight;;
                                    if(iy>=height) iy = (iy+height)%height;
                                    if(iz>=depth) iz = (iz+depth)%depth;
                                }
                                int imgIndex = iz*height*weight +iy*weight +ix;
                                int kernelIndex = (kk+kernelD/2)*kernelH*kernelW
                                         + (jj+kernelH/2)*kernelW + (ii+kernelW/2);
                                intensity += matrix[kernelIndex]*inPixels[imgIndex];
                            }
                        }
                    }

                    outPixels[index] = (int) intensity;
                }
            }
        }
    }

    public Kernel makeKernel(int radius, float sig){
        int kernelSize = 0;
        if(this.d == Type.D1){
            kernelSize = radius;
        }else if(this.d == Type.D2){
            kernelSize = radius*radius;
        }else {
            kernelSize = radius*radius*radius;
        }

        float[] kernel = new float[kernelSize];

        float sigma = sig;

        float sigma22 = 2*sigma*sigma;
        float sigmaPi2 = (float) (2*Math.PI*sigma);
        float sqrtSigmaPi2 = (float) Math.sqrt(sigmaPi2);

        float total = 0;

        if(this.d == Type.D1){
            int centerX = radius/2;
            for(int weight=0; weight<radius; weight++){
                float distance = Math.abs(centerX - weight);
                kernel[weight] = (float) Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
                total += kernel[weight];
            }
        }else if(this.d == Type.D2){
            int centerX = radius/2;
            int centerY = radius/2;
            for(int height=0; height<radius; height++){
                for(int weight=0; weight<radius; weight++){
                    int index = height*radius + weight;
                    float distance = (float) Math.sqrt(Math.pow((centerX - weight),2) + Math.pow((centerY - height),2));
                    kernel[index] = (float) Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
                    total += kernel[index];
                }
            }
        }else {
            int centerX = radius/2;
            int centerY = radius/2;
            int centerZ = radius/2;
            for(int depth=0; depth<radius; depth++){
                for(int height=0; height<radius; height++){
                    for(int weight=0; weight<radius; weight++){
                        int index = depth*radius*radius + height*radius + weight;
                        float distance = (float) Math.sqrt(Math.pow((centerX - weight),2)
                                + Math.pow((centerY - height),2) + Math.pow((centerZ-depth),2));
                        kernel[index] = (float) Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
                        total += kernel[index];
                    }
                }
            }
        }

        for(int i=0; i<kernel.length; i++){
            kernel[i] /= total;
        }

        if(this.d == Type.D1){
            return new Kernel(kernel,radius,1,1);
        }else if(this.d == Type.D2){
            return new Kernel(kernel,radius,radius,1);
        }else {
            return new Kernel(kernel,radius,radius,radius);
        }

    }

    public Kernel getKernel() {
        return kernel;
    }
}
