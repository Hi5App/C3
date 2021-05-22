package com.penglab.hi5.basic.tracingfunc.gd;

public class BoundingBox {
    public float x0, y0, z0;
    public float x1, y1, z1;

    public BoundingBox() {x0=y0=z0=x1=y1=z1 = 0;}
    public BoundingBox(float px0, float py0, float pz0, float px1, float py1, float pz1){x0=px0; y0=py0; z0=pz0;  x1=px1; y1=py1; z1=pz1;}
//    public BoundingBox(XYZ pV0, XYZ pV1){x0=pV0.x; y0=pV0.y; z0=pV0.z;  x1=pV1.x; y1=pV1.y; z1=pV1.z;}
    // float Dx() {return (x1-x0); }
    // float Dy() {return (y1-y0); }
    // float Dz() {return (z1-z0); }
    // float Dmin() {return MIN(MIN(Dx(),Dy()),Dz());}
    // float Dmax() {return MAX(MAX(Dx(),Dy()),Dz());}
    // XYZ V0() {return XYZ(x0,y0,z0);}
    // XYZ V1() {return XYZ(x1,y1,z1);}
    // XYZ Vabsmin() {return XYZ(ABSMIN(x0,x1), ABSMIN(y0,y1), ABSMIN(z0,z1));}
    // XYZ Vabsmax() {return XYZ(ABSMAX(x0,x1), ABSMAX(y0,y1), ABSMAX(z0,z1));}
    // boolean isNegtive()	{return (Dx()<0 || Dy()<0 || Dz()<0);}
    // boolean isInner(XYZ V, float d=0) 	{
    // 	return BETWEENEQ(x0-d,x1+d, V.x) && BETWEENEQ(y0-d,y1+d, V.y) && BETWEENEQ(z0-d,z1+d, V.z);
    // }
    // void clamp(XYZ & V) {
    // 	V.x = CLAMP(x0, x1, V.x); V.y = CLAMP(y0, y1, V.y); V.z = CLAMP(z0, z1, V.z);
    // }
    // void expand(XYZ V) {
    // 	if (Dx()<0) { x0 = x1 = V.x;} else { x0=MIN(x0, V.x); x1=MAX(x1, V.x); }
    // 	if (Dy()<0) { y0 = y1 = V.y;} else { y0=MIN(y0, V.y); y1=MAX(y1, V.y); }
    // 	if (Dz()<0) { z0 = z1 = V.z;} else { z0=MIN(z0, V.z); z1=MAX(z1, V.z); }
    // }
    // void expand(BoundingBox B) {
    // 	if (B.isNegtive()) return;
    // 	expand(B.V0()); expand(B.V1());
    // }
    // void shift(float x, float y, float z) 	{x0+=x; y0+=y; z0+=z;	x1+=x; y1+=y; z1+=z;}
    // void shift(XYZ S) 						{shift(S.x, S.y, S.z);}
}
