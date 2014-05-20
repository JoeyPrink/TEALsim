package teal.math;

public class Fourier {
	public static Complex2d [] dft( Complex2d [] x ) {
		int N = x.length;
		Complex2d [] X = new Complex2d[N];
		Complex2d W = Complex2d.exp(new Complex2d(0.,-2.*Math.PI/(double)N));
		Complex2d Wk = new Complex2d(1.,0.);
		Complex2d tmp = new Complex2d();
		for(int k=0; k<N; k++) {
			Complex2d Wkn = new Complex2d(1.,0.);
			X[k] = new Complex2d();
			for(int n=0; n<N; n++) {
				tmp.set(x[n]);
				tmp.mul(Wkn);
				X[k].add(tmp);
				Wkn.mul(Wk);
			}
			Wk.mul(W); 
		}
		return X;
	}

	public static Complex2d dft( Complex2d [] x, int k ) {
		int N = x.length;
		Complex2d X = new Complex2d();
		Complex2d Wk = Complex2d.exp(new Complex2d(0.,-2.*Math.PI*(double)k/(double)N));
		Complex2d tmp = new Complex2d();
		Complex2d Wkn = new Complex2d(1.,0.);
		for(int n=0; n<N; n++) {
			tmp.set(x[n]);
			tmp.mul(Wkn);
			X.add(tmp);
			Wkn.mul(Wk);
		}
		return X;
	}
	
	public static Complex2d [] idft( Complex2d [] X ) {
		int N = X.length;
		double factor = 1./(double)N;
		Complex2d [] x = new Complex2d[N];
		Complex2d W = Complex2d.exp(new Complex2d(0.,2.*Math.PI/(double)N));
		Complex2d Wn = new Complex2d(1.,0.);
		Complex2d tmp = new Complex2d();
		for(int n=0; n<N; n++) {
			Complex2d Wnk = new Complex2d(1.,0.);
			x[n] = new Complex2d();
			for(int k=0; k<N; k++) {
				tmp.set(X[k]);
				tmp.mul(Wnk);
				x[n].add(tmp);
				Wnk.mul(Wn);
			}
			Wn.mul(W); 
			x[n].scale(factor);
		}
		return x;
	}

	public static Complex2d idft( Complex2d [] X, int n ) {
		int N = X.length;
		double factor = 1./(double)N;
		Complex2d x = new Complex2d();
		Complex2d Wn = Complex2d.exp(new Complex2d(0.,2.*Math.PI*(double)n/(double)N));
		Complex2d tmp = new Complex2d();
		Complex2d Wnk = new Complex2d(1.,0.);
		for(int k=0; k<N; k++) {
			tmp.set(X[k]);
			tmp.mul(Wnk);
			x.add(tmp);
			Wnk.mul(Wn);
		}
		x.scale(factor);
		return x;
	}

	public static Complex2d idft( Complex2d [] X, double t ) {
		int N = X.length;
		double factor = 1./(double)N;
		Complex2d x = new Complex2d();
		Complex2d Wn = Complex2d.exp(new Complex2d(0.,t));
		Complex2d WNt = Complex2d.exp(new Complex2d(0.,-N*t));
		Complex2d tmp = new Complex2d();
		Complex2d Wnk = new Complex2d(1.,0.);
		for(int k=0; k<Math.ceil(N/2)-1; k++) {
			tmp.set(X[k]);
			tmp.mul(Wnk);
			x.add(tmp);
			Wnk.mul(Wn);
		}
		Wnk.mul(WNt);
		for(int k=(int)Math.ceil(N/2)-1; k<N; k++) {
			tmp.set(X[k]);
			tmp.mul(Wnk);
			x.add(tmp);
			Wnk.mul(Wn);
		}
		x.scale(factor);
		return x;
	}
	
	public static Complex2d [] dft( double [] x ) {
		int N = x.length;
		Complex2d [] x_ = new Complex2d[N];
		for(int n=0; n<N; n++) {
			x_[n] = new Complex2d(x[n],0.);
		}
		return dft(x_);
	}
	
	public static Complex2d dft( double [] x, int k ) {
		int N = x.length;
		Complex2d [] x_ = new Complex2d[N];
		for(int n=0; n<N; n++) {
			x_[n] = new Complex2d(x[n],0.);
		}
		return dft(x_,k);
	}

	
	public static Complex2d [] idft( double [] X ) {
		int N = X.length;
		Complex2d [] X_ = new Complex2d[N];
		for(int k=0; k<N; k++) {
			X_[k] = new Complex2d(X[k],0.);
		}
		return idft(X_);
	}

	public static Complex2d idft( double [] X, int n) {
		int N = X.length;
		Complex2d [] X_ = new Complex2d[N];
		for(int k=0; k<N; k++) {
			X_[k] = new Complex2d(X[k],0.);
		}
		return idft(X_,n);
	}

	public static Complex2d idft( double [] X, double t ) {
		int N = X.length;
		Complex2d [] X_ = new Complex2d[N];
		for(int k=0; k<N; k++) {
			X_[k] = new Complex2d(X[k],0.);
		}
		return idft(X_,t);
	}

	// ***********************************************************************
	// Utility method.
	// ***********************************************************************

	public static double wrap(double angle) {
		return angle-Math.floor(angle/(2.*Math.PI))*(2.*Math.PI);
	}
	
	public static double step(double t) {
		if(t>0) {
			return 1.;
		} else if (t<0) {
			return 0.;
		}
		return 0.5;
	}

	//***********************************************************************
	// Experimental methods for reducing computation. 
	//***********************************************************************
	
	public static Complex2d [] optimized_dft( double [] x, int [] m ) {
		int N = x.length;
		Complex2d [] x_ = new Complex2d[N];
		for(int n=0; n<N; n++) {
			x_[n] = new Complex2d(x[n],0.);
		}
		Complex2d[] X = fft(x_);

		double max = 0.;
		for(int k=0; k<N; k++) {
			double mag = X[k].getAbs();
			if(mag>max) max = mag;
		}
		int M = (int) Math.ceil((double)N/2.);
		for(int k=M-1; k>=0; k--) {
			if(X[k].getAbs() > 0.001*max) {
				m[0] = k;
				break;
			}
		}
		for(int k=M; k<N; k++) {
			if(X[k].getAbs() > 0.001*max) {
				m[1] = k;
				break;
			}
		}
		return X;
	}
	
	public static Complex2d optimized_idft( Complex2d [] X, double t, int [] m ) {
		int N = X.length;
		double factor = 1./(double)N;
		Complex2d x = new Complex2d();
		Complex2d Wn = Complex2d.exp(new Complex2d(0.,t));
		Complex2d Wm_Nt = Complex2d.exp(new Complex2d(0.,(m[1]-N)*t));
		Complex2d tmp = new Complex2d();
		Complex2d Wnk = new Complex2d(1.,0.);
		for(int k=0; k<=m[0]; k++) {
			tmp.set(X[k]);
			tmp.mul(Wnk);
			x.add(tmp);
			Wnk.mul(Wn);
		}
		Wnk.set(Wm_Nt);
//		for(int k=M; k<N; k++) {
		for(int k=m[1]; k<N; k++) {
			tmp.set(X[k]);
			tmp.mul(Wnk);
			x.add(tmp);
			Wnk.mul(Wn);
		}
		x.scale(factor);
		return x;
	}

	
	
	public static Complex2d [] fft( Complex2d [] x ) {
		
		int N = x.length;
		float [] ar = new float [N];
		float [] ai = new float [N];
		for(int n=0; n<N; n++) {
			ar[n] = (float) x[n].real();
			ai[n] = (float) x[n].imag();
		}
		complexToComplex(-1, N, ar, ai);
		Complex2d [] X = new Complex2d[N];
		for(int k=0; k<N; k++) {
			X[k] = new Complex2d(ar[k], ai[k]);
		}
/*		Complex2d [] X = new Complex2d[N];
		Complex2d W = Complex2d.exp(new Complex2d(0.,-2.*Math.PI/(double)N));
		Complex2d Wk = new Complex2d(1.,0.);
		Complex2d tmp = new Complex2d();
		for(int k=0; k<N; k++) {
			Complex2d Wkn = new Complex2d(1.,0.);
			X[k] = new Complex2d();
			for(int n=0; n<N; n++) {
				tmp.set(x[n]);
				tmp.mul(Wkn);
				X[k].add(tmp);
				Wkn.mul(Wk);
			}
			Wk.mul(W); 
		}
*/		return X;
	}	
	
	
	
	
	public static void complexToComplex(int sign, int n, float ar[], float ai[]) {
		float scale = 1f; // (float) Math.sqrt(1.0f / n);
		int i, j;
		for (i = j = 0; i < n; ++i) {
			if (j >= i) {
				float tempr = ar[j] * scale;
				float tempi = ai[j] * scale;
				ar[j] = ar[i] * scale;
				ai[j] = ai[i] * scale;
				ar[i] = tempr;
				ai[i] = tempi;
			}
			int m = n / 2;
			while (m >= 1 && j >= m) {
				j -= m;
				m /= 2;
			}
			j += m;
		}
		int mmax, istep;
		for (mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax) {
			float delta = (float) sign * 3.141592654f / (float) mmax;
			for (int m = 0; m < mmax; ++m) {
				float w = (float) m * delta;
				float wr = (float) Math.cos(w);
				float wi = (float) Math.sin(w);
				for (i = m; i < n; i += istep) {
					j = i + mmax;
					float tr = wr * ar[j] - wi * ai[j];
					float ti = wr * ai[j] + wi * ar[j];
					ar[j] = ar[i] - tr;
					ai[j] = ai[i] - ti;
					ar[i] += tr;
					ai[i] += ti;
				}
			}
			mmax = istep;
		}
	}


}

