package com.penglab.hi5.basic.learning.filter;

import com.penglab.hi5.basic.learning.filter.Kernel1D.BorderTreatmentMode;

public class SeparableConvolution {
    public static void internalConvolveLineOptimistic(int[] src, int srcStart, int srcEnd,
                                                      int[] dest, int destStart,
                                                      Kernel1D kernel1D, int kLeft, int kRight){
        int w = srcEnd - srcStart;
        int kw = kRight - kLeft + 1;
        for(int x=0; x<w; ++x, ++srcStart, ++destStart){
            int iss = srcStart + (-kRight);
            int ik = kRight;
            double sum = 0.0;
            for(int k=0; k<kw; ++k, --ik, ++iss){
                sum += kernel1D.getElement(ik)*src[iss];
            }
            dest[destStart+x] = (int) sum;
        }
    }

    public static void copyLineWithBorderTreatment(int[] src, int srcStart, int srcEnd,
                                                   int[] dest, int destStart,
                                                   int start, int stop,
                                                   int kLeft, int kRight,
                                                   BorderTreatmentMode borderTreatmentMode){
        int w = srcEnd - srcStart;
        int leftBorder = start - kRight;
        int rightBorder = stop - kLeft;
        int copyEnd = Math.min(w,rightBorder);

        if(leftBorder<0){
            switch (borderTreatmentMode){
                case BORDER_TREATMENT_WRAP:
                    for(;leftBorder<0;++leftBorder,++destStart){
                        dest[destStart] = src[srcEnd+leftBorder];
                    }
                    break;
                case BORDER_TREATMENT_AVOID:
                    break;
                case BORDER_TREATMENT_REFLECT:
                    for(;leftBorder<0;++leftBorder,++destStart){
                        dest[destStart] = src[srcStart-leftBorder];
                    }
                    break;
                case BORDER_TREATMENT_REPEAT:
                    for(;leftBorder<0;++leftBorder,++destStart){
                        dest[destStart] = src[srcStart];
                    }
                    break;
                case BORDER_TREATMENT_CLIP:
                    System.out.println("copyLineWithBorderTreatment() internal error: not applicable to BORDER_TREATMENT_CLIP");
                    break;
                case BORDER_TREATMENT_ZEROPAD:
                    for(;leftBorder<0;++leftBorder,++destStart){
                        dest[destStart] = 0;
                    }
                    break;
                default:
                    System.out.println("copyLineWithBorderTreatment(): Unknown border treatment mode");
            }
        }

        int iss = srcStart + leftBorder;
        if(leftBorder>=copyEnd)
            System.out.println("copyLineWithBorderTreatment(): assertion failed");
        for(; leftBorder<copyEnd; ++leftBorder, ++destStart, ++iss){
            dest[destStart] = src[iss];
        }

        if(copyEnd<rightBorder){
            switch (borderTreatmentMode){
                case BORDER_TREATMENT_WRAP:
                    for(;copyEnd<rightBorder; ++copyEnd, ++destStart, ++srcStart){
                        dest[destStart] = src[srcStart];
                    }
                    break;
                case BORDER_TREATMENT_AVOID:
                    break;
                case BORDER_TREATMENT_REFLECT:
                    iss -= 2;
                    for(;copyEnd<rightBorder; ++copyEnd, ++destStart, --iss){
                        dest[destStart] = src[iss];
                    }
                    break;
                case BORDER_TREATMENT_REPEAT:
                    --iss;
                    for(;copyEnd<rightBorder; ++copyEnd, ++destStart){
                        dest[destStart] = src[iss];
                    }
                    break;
                case BORDER_TREATMENT_CLIP:
                    System.out.println("copyLineWithBorderTreatment() internal error: not applicable to BORDER_TREATMENT_CLIP");
                    break;
                case BORDER_TREATMENT_ZEROPAD:
                    for(;copyEnd<rightBorder; ++copyEnd, ++destStart){
                        dest[destStart] = 0;
                    }
                    break;
                default:
                    System.out.println("copyLineWithBorderTreatment(): Unknown border treatment mode");

            }
        }

    }

    public static void internalConvolveLineWrap(int[] src, int srcStart, int srcEnd, int[] mask,
                                                int[] dest, int destStart,
                                                Kernel1D kernel1D,
                                                int kLeft, int kRight,
                                                int start, int stop){
        int w = srcEnd - srcStart;

        int iBegin = srcStart;
        if(stop == 0)
            stop = w;
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            int ik = kRight;
            double sum = 0.0;
            if(x<kRight){
                int x0 = x - kRight;
                int iss = srcEnd + x0;
                for(; x0>0; ++x0, --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                iss = iBegin;
                if(w-x <= -kLeft){
                    int isend = srcEnd;
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }

                    int x1 = -kLeft-w+x+1;
                    iss = iBegin;
                    for (; x1>0; --x1, --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }else {
                    int isend = srcStart + (1-kLeft);
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }
            }
            else if(w-x <= -kLeft){
                int iss = srcStart + (-kRight);
                int isend = srcEnd;
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                int x1 = -kLeft-w+x+1;
                iss = iBegin;
                for (; x1>0; --x1, --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            else {
                int iss = srcStart + (-kRight);
                int isend = srcStart + (1-kLeft);
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void internalConvolveLineClip(int[] src, int srcStart, int srcEnd, int[] mask,
                                                int[] dest, int destStart,
                                                Kernel1D kernel1D,
                                                int kLeft, int kRight, double norm,
                                                int start, int stop){
        int w = srcEnd - srcStart;

        int iBegin = srcStart;
        if(stop == 0)
            stop = w;
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            int ik = kRight;
            double sum = 0.0;
            if(x<kRight){
                int x0 = x - kRight;
                double clipped = 0.0;
                for(; x0>0; ++x0, --ik){
                    clipped += kernel1D.getElement(ik);
                }
                int iss = iBegin;

                if(w-x <= -kLeft){
                    int isend = srcEnd;
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }

                    int x1 = -kLeft-w+x+1;

                    for (; x1>0; --x1, --ik, ++iss){
                        clipped+= kernel1D.getElement(ik);
                    }
                }else {
                    int isend = srcStart + (1-kLeft);
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }
                sum = norm/(norm-clipped)*sum;
            }
            else if(w-x <= -kLeft){
                int iss = srcStart + (-kRight);
                int isend = srcEnd;
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                double clipped = 0.0;

                int x1 = -kLeft-w+x+1;

                for (; x1>0; --x1, --ik, ++iss){
                    clipped+= kernel1D.getElement(ik);
                }
                sum = norm/(norm-clipped)*sum;
            }
            else {
                int iss = srcStart + (-kRight);
                int isend = srcStart + (1-kLeft);
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void internalConvolveLineZeropad(int[] src, int srcStart, int srcEnd, int[] mask,
                                                int[] dest, int destStart,
                                                Kernel1D kernel1D,
                                                int kLeft, int kRight,
                                                int start, int stop){
        int w = srcEnd - srcStart;

        int iBegin = srcStart;
        if(stop == 0)
            stop = w;
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            double sum = 0.0;
            if(x<kRight){
                int ik = kRight;
                int iss = iBegin;

                if(w-x <= -kLeft){
                    int isend = srcEnd;
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }else {
                    int isend = srcStart + (1-kLeft);
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }
            }
            else if(w-x <= -kLeft){
                int ik = kRight;
                int iss = srcStart + (-kRight);
                int isend = srcEnd;
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            else {
                int ik = kRight;
                int iss = srcStart + (-kRight);
                int isend = srcStart + (1-kLeft);
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void internalConvolveLineReflect(int[] src, int srcStart, int srcEnd, int[] mask,
                                                int[] dest, int destStart,
                                                Kernel1D kernel1D,
                                                int kLeft, int kRight,
                                                int start, int stop){
        int w = srcEnd - srcStart;

        int iBegin = srcStart;
        if(stop == 0)
            stop = w;
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            int ik = kRight;
            double sum = 0.0;
            if(x<kRight){
                int x0 = x - kRight;
                int iss = iBegin - x0;
                for(; x0>0; ++x0, --ik, --iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                iss = iBegin;
                if(w-x <= -kLeft){
                    int isend = srcEnd;
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }

                    int x1 = -kLeft-w+x+1;
                    iss = srcEnd - 2;
                    for (; x1>0; --x1, --ik, --iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }else {
                    int isend = srcStart + (1-kLeft);
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }
            }
            else if(w-x <= -kLeft){
                int iss = srcStart + (-kRight);
                int isend = srcEnd;
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                int x1 = -kLeft-w+x+1;
                iss = srcEnd - 2;
                for (; x1>0; --x1, --ik, --iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            else {
                int iss = srcStart + (-kRight);
                int isend = srcStart + (1-kLeft);
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void internalConvolveLineRepeat(int[] src, int srcStart, int srcEnd, int[] mask,
                                                int[] dest, int destStart,
                                                Kernel1D kernel1D,
                                                int kLeft, int kRight,
                                                int start, int stop){
        int w = srcEnd - srcStart;

        int iBegin = srcStart;
        if(stop == 0)
            stop = w;
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            int ik = kRight;
            double sum = 0.0;
            if(x<kRight){
                int x0 = x - kRight;
                int iss = iBegin;
                for(; x0>0; ++x0, --ik){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                iss = iBegin;
                if(w-x <= -kLeft){
                    int isend = srcEnd;
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }

                    int x1 = -kLeft-w+x+1;
                    iss = srcEnd - 1;
                    for (; x1>0; --x1, --ik){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }else {
                    int isend = srcStart + (1-kLeft);
                    for(; iss != isend; --ik, ++iss){
                        sum += kernel1D.getElement(ik)*src[iss];
                    }
                }
            }
            else if(w-x <= -kLeft){
                int iss = srcStart + (-kRight);
                int isend = srcEnd;
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }

                int x1 = -kLeft-w+x+1;
                iss = srcEnd - 1;
                for (; x1>0; --x1, --ik){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            else {
                int iss = srcStart + (-kRight);
                int isend = srcStart + (1-kLeft);
                for(; iss != isend; --ik, ++iss){
                    sum += kernel1D.getElement(ik)*src[iss];
                }
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void internalConvolveLineAvoid(int[] src, int srcStart, int srcEnd, int[] mask,
                                                  int[] dest, int destStart,
                                                  Kernel1D kernel1D,
                                                  int kLeft, int kRight,
                                                  int start, int stop){
        int w = srcEnd - srcStart;

        if(start<stop){
            if(w+kLeft<stop)
                stop = w +kLeft;
            if(start<kRight){
                destStart += kRight - start;
                start = kRight;
            }
        }else {
            destStart += kRight;
            start = kRight;
            stop = w +kLeft;
        }
        srcStart += start;

        for(int x=start; x<stop; ++x, ++srcStart, ++destStart){
            if(mask[srcStart] == 0){
                dest[destStart] = src[srcStart];
                continue;
            }
            int ik = kRight;
            double sum = 0.0;

            int iss = srcStart + (-kRight);
            int isend = srcStart + (1-kLeft);
            for(; iss != isend; --ik, ++iss){
                sum += kernel1D.getElement(ik)*src[iss];
            }
            dest[destStart] = (int) sum;
        }
    }

    public static void convolveLine(int[] src, int srcStart, int srcEnd, int[] mask,
                                                 int[] dest, int destStart,
                                                 Kernel1D kernel1D,
                                                 int kLeft, int kRight, BorderTreatmentMode border,
                                                 int start, int stop) {
        int w = srcEnd - srcStart;
        if(w<Math.max(kLeft,kRight)+1){
            System.out.println("convolveLine(): kernel longer than line");
            return;
        }
        if(stop != 0){
            if(0<=start && start<stop && stop<=w){
                System.out.println("convolveLine(): invalid subrange (start, stop)");
                return;
            }
        }
        switch (border){
            case BORDER_TREATMENT_WRAP:
                internalConvolveLineWrap(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,start,stop);
                break;
            case BORDER_TREATMENT_AVOID:
                internalConvolveLineAvoid(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,start,stop);
                break;
            case BORDER_TREATMENT_REFLECT:
                internalConvolveLineReflect(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,start,stop);
                break;
            case BORDER_TREATMENT_REPEAT:
                internalConvolveLineRepeat(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,start,stop);
                break;
            case BORDER_TREATMENT_CLIP:
                double norm = 0.0;
                for(int i=kLeft; i<=kRight; i++){
                    norm += kernel1D.getElement(i);
                }
                if(norm == 0){
                    System.out.println("convolveLine(): Norm of kernel must be != 0");
                    return;
                }
                internalConvolveLineClip(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,norm,start,stop);
            case BORDER_TREATMENT_ZEROPAD:
                internalConvolveLineZeropad(src,srcStart,srcEnd,mask,dest,destStart,kernel1D,kLeft,kRight,start,stop);
                break;
            default:
                System.out.println("convolveLine(): UnKnown border treatment mode");
        }

    }

    public static void convolveLine(int[] src, int[] mask, int[] dest, Kernel1D kernel1D){
        convolveLine(src,0,src.length,mask,dest,0,kernel1D,kernel1D.getLeft(),kernel1D.getRight(),kernel1D.getBorderTreatmentMode(),0,0);
    }
}
