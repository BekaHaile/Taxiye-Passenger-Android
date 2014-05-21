/*     */ package product.clicklabs.jugnoo;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.util.DisplayMetrics;
/*     */ import android.util.Log;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import android.view.ViewGroup.MarginLayoutParams;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.TextView;
/*     */ 
/*     */ public class ASSL
/*     */ {
/*     */   ViewGroup rv;
/*     */   private Activity context;
/*     */   static float scaleX;
/*     */   static float scaleY;
/*     */   float xx;
/*     */   static int height;
/*     */   static int width;
/*  25 */   float density = 0.0F;
/*     */   int heightp;
/*     */   int widthp;
/*     */ 
/*     */   public ASSL(Activity Context, ViewGroup rootLayout, int layoutHeight, int layoutWidth, Boolean isFullscrenn)
/*     */   {
/*  33 */     this.context = Context;
/*  34 */     this.rv = rootLayout;
/*  35 */     this.heightp = layoutHeight;
/*  36 */     this.widthp = layoutWidth;
/*     */ 
/*  38 */     height = this.context.getResources().getDisplayMetrics().heightPixels;
/*  39 */     width = this.context.getResources().getDisplayMetrics().widthPixels;
/*  40 */     this.density = this.context.getResources().getDisplayMetrics().density;
/*     */ 
/*  42 */     if (!isFullscrenn.booleanValue()) {
/*  43 */       height -= (int)(24.0F * this.density);
/*     */     }

/*     */ 
/*  57 */     if (this.context.getPackageName().toString().equalsIgnoreCase("product.clicklabs.jugnoo")) {
/*  58 */       Log.e("ASSL (RMN)", 
/*  59 */         "Android Screen Size Library");
/*  60 */       scale();
/*     */     }
/*     */     else
/*     */     {
/*  64 */       Log.e("Android Screen Size SDK  = ", 
/*  65 */         "Oops, we cought you cheater :)");
/*  66 */       Log.e("Android Screen Size SDK", 
/*  67 */         "Your app is unauthorized to use this SDK");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scale()
/*     */   {
/*  94 */     scaleX = ((float)width) / ((float)this.widthp);
/*  95 */     scaleY = ((float)height) / ((float)this.heightp);
/* 100 */     DoMagic(this.rv);
/*     */   }
/*     */ 
/*     */   public static float Xscale()
/*     */   {
/* 105 */     return scaleX;
/*     */   }
/*     */ 
/*     */   public static float Yscale() {
/* 109 */     return scaleY;
/*     */   }
/*     */ 
/*     */   public static void closeActivity(View rootLayout)
/*     */   {
/* 116 */     if (rootLayout.getBackground() != null) {
/* 117 */       rootLayout.getBackground().setCallback(null);
/*     */     }
/*     */ 
/* 120 */     if (((rootLayout instanceof ViewGroup)) && (!(rootLayout instanceof AdapterView))) {
/* 121 */       for (int i = 0; i < ((ViewGroup)rootLayout).getChildCount(); i++)
/* 122 */         closeActivity(((ViewGroup)rootLayout).getChildAt(i));
/*     */       try
/*     */       {
/* 125 */         ((ViewGroup)rootLayout).removeAllViews();
/*     */       } catch (UnsupportedOperationException localUnsupportedOperationException) {
/*     */       }
/*     */     }
/*     */     else {
/* 130 */       rootLayout = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void DoMagic(View rootLayout)
/*     */   {
/* 145 */     if ((rootLayout instanceof TextView))
/*     */     {
/* 150 */       TextView tv = (TextView)rootLayout;
/*     */ 
/* 155 */       float k = tv.getTextSize() * scaleX;
/*     */ 
/* 157 */       tv.setTextSize(0, k);
/*     */     }
/*     */ 
/* 162 */     ViewGroup.LayoutParams layoutParams = rootLayout.getLayoutParams();
/*     */ 
/* 165 */     if (rootLayout.getTag() != null)
/*     */     {
/* 168 */       if (rootLayout.getTag().toString().equals("mwar"))
/*     */       {
/* 172 */         if ((layoutParams.width != -2) && 
/* 173 */           (layoutParams.width != -1) && 
/* 174 */           (layoutParams.width != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp80_79 = layoutParams; tmp80_79.width = ((int)(tmp80_79.width * scaleX));
/* 176 */         }if ((layoutParams.height != -2) && 
/* 177 */           (layoutParams.height != -1) && 
/* 178 */           (layoutParams.height != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp119_118 = layoutParams; tmp119_118.height = ((int)(tmp119_118.height * scaleX));
/*     */         }
/*     */ 
/*     */       }
/* 183 */       else if (rootLayout.getTag().toString().equals("mhar"))
/*     */       {
/* 186 */         if ((layoutParams.width != -2) && 
/* 187 */           (layoutParams.width != -1) && 
/* 188 */           (layoutParams.width != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp176_175 = layoutParams; tmp176_175.width = ((int)(tmp176_175.width * scaleY));
/* 190 */         }if ((layoutParams.height != -2) && 
/* 191 */           (layoutParams.height != -1) && 
/* 192 */           (layoutParams.height != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp215_214 = layoutParams; tmp215_214.height = ((int)(tmp215_214.height * scaleY));
/*     */         }
/*     */ 
/*     */       }
/* 197 */       else if (rootLayout.getTag().toString().equals("mlar"))
/*     */       {
/* 201 */         if (scaleY < scaleX) {
/* 202 */           if ((layoutParams.width != -2) && 
/* 203 */             (layoutParams.width != -1) && 
/* 204 */             (layoutParams.width != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp282_281 = layoutParams; tmp282_281.width = ((int)(tmp282_281.width * scaleY));
/* 206 */           }if ((layoutParams.height != -2) && 
/* 207 */             (layoutParams.height != -1) && 
/* 208 */             (layoutParams.height != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp321_320 = layoutParams; tmp321_320.height = ((int)(tmp321_320.height * scaleY));
/*     */           }
/*     */         } else { if ((layoutParams.width != -2) && 
/* 212 */             (layoutParams.width != -1) && 
/* 213 */             (layoutParams.width != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp363_362 = layoutParams; tmp363_362.width = ((int)(tmp363_362.width * scaleX));
/* 215 */           }if ((layoutParams.height != -2) && 
/* 216 */             (layoutParams.height != -1) && 
/* 217 */             (layoutParams.height != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp402_401 = layoutParams; tmp402_401.height = ((int)(tmp402_401.height * scaleX));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 232 */         if ((layoutParams.width != -2) && 
/* 233 */           (layoutParams.width != -1) && 
/* 234 */           (layoutParams.width != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp444_443 = layoutParams; tmp444_443.width = ((int)(tmp444_443.width * scaleX));
/*     */         }
/*     */ 
/* 242 */         if ((layoutParams.height != -2) && 
/* 243 */           (layoutParams.height != -1) && 
/* 244 */           (layoutParams.height != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp483_482 = layoutParams; tmp483_482.height = ((int)(tmp483_482.height * scaleY));
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 250 */       if ((layoutParams.width != -2) && 
/* 251 */         (layoutParams.width != -1) && 
/* 252 */         (layoutParams.width != -1))
/*     */       {
/*     */         ViewGroup.LayoutParams tmp525_524 = layoutParams; tmp525_524.width = ((int)(tmp525_524.width * scaleX));
/*     */       }
/*     */ 
/* 259 */       if ((layoutParams.height != -2) && 
/* 260 */         (layoutParams.height != -1) && 
/* 261 */         (layoutParams.height != -1))
/*     */       {
/*     */         ViewGroup.LayoutParams tmp564_563 = layoutParams; tmp564_563.height = ((int)(tmp564_563.height * scaleY));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 268 */     if ((layoutParams instanceof ViewGroup.MarginLayoutParams))
/*     */     {
/* 270 */       ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)layoutParams;
/*     */       ViewGroup.MarginLayoutParams tmp590_589 = marginParams; tmp590_589.leftMargin = ((int)(tmp590_589.leftMargin * scaleX));
/*     */       ViewGroup.MarginLayoutParams tmp604_603 = marginParams; tmp604_603.topMargin = ((int)(tmp604_603.topMargin * scaleY));
/*     */       ViewGroup.MarginLayoutParams tmp618_617 = marginParams; tmp618_617.rightMargin = ((int)(tmp618_617.rightMargin * scaleX));
/*     */       ViewGroup.MarginLayoutParams tmp632_631 = marginParams; tmp632_631.bottomMargin = ((int)(tmp632_631.bottomMargin * scaleY));
/*     */     }
/*     */ 
/* 282 */     rootLayout.setLayoutParams(layoutParams);
/*     */ 
/* 286 */     rootLayout.setPadding(
/* 288 */       (int)(rootLayout.getPaddingLeft() * scaleX), 
/* 290 */       (int)(rootLayout.getPaddingTop() * scaleY), 
/* 292 */       (int)(rootLayout.getPaddingRight() * scaleX), 
/* 294 */       (int)(rootLayout.getPaddingBottom() * scaleY));
/*     */ 
/* 314 */     if ((rootLayout instanceof ViewGroup))
/*     */     {
/* 316 */       ViewGroup vg = (ViewGroup)rootLayout;
/*     */ 
/* 318 */       for (int i = 0; i < vg.getChildCount(); i++)
/* 319 */         if (vg.getChildAt(i).getTag() != null)
/*     */         {
/* 321 */           if (!vg.getChildAt(i).getTag().toString()
/* 321 */             .equalsIgnoreCase("DontTouchMe"))
/* 322 */             DoMagic(vg.getChildAt(i));
/*     */         }
/*     */         else
/* 325 */           DoMagic(vg.getChildAt(i));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/shankar/Desktop/assl_sdk_rmn.jar
 * Qualified Name:     rmn.androidscreenlibrary.ASSL
 * JD-Core Version:    0.6.2
 */