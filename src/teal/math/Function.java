/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Function.java,v 1.16 2007/07/16 22:04:47 pbailey Exp $
 * 
 */

package teal.math;

import java.io.Serializable;

public class Function implements Serializable
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2395370687066079286L;
	private final static int FMAXLENGTH = 640;
	private char [] fixed = new char[FMAXLENGTH];
	private char [] raw = new char[FMAXLENGTH];
	private boolean parsed = false;

	private boolean isoper(char c) {
		return (	c=='U'|| c=='V'||c=='W' ||
					c=='X'|| c=='Y'||c=='Z' ||
					c=='E'|| c=='T'||c=='C' ||c=='S' ||c=='+' ||c=='-' ||
					c=='L'|| c=='A'||c=='*' ||c=='/' ||c=='^' ||c=='%' ||
					c=='N'|| c=='Q'||c=='B' );
	}

	private int precedes(char op1, char op2) {
		if (!isoper(op1) || !isoper(op2))
			return -2;
		char ops[] =
			{ 'A', 'B', 'E', 'N', 'L', 'S', 'C', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Q', '^', '%', '/', '*', '-', '+', '\0' };
		int p1 = 0, p2 = 0;
		for (int i = 0; ops[i] != '\0'; i++) {
			if (op1 == ops[i])
				p1 = i;
			if (op2 == ops[i])
				p2 = i;
		}
		if (p1 == p2)
			return 0;
		if (p1 < p2)
			return 1;
		if (p1 > p2)
			return -1;
		return 2;
	}

	private void ctop() {
		try {
			CharStack s = new CharStack();
			// NOTE errors may occur, might need to increase the size.
			char[] postfix = new char[FMAXLENGTH + 50];
			// I might need to explicitly set all characters to the zero character.
			System.out.println("Before preprocessing:" + cleanStringOf(fixed));
			preprocess1();
			preprocess2();
			System.out.println("After preprocessing:" + cleanStringOf(fixed));
			int i, pfxTop = 0;
			s.push('(');
			for (i = 0; fixed[i] != '\0'; i++);
			fixed[i] = ')';
			fixed[i + 1] = '\0';
			int iPutOp = 0;

//			while (!s.empty()) {
				for (i = 0; fixed[i] != '\0'; i++) {
					if (Character.isWhitespace(fixed[i]))
						continue;
					if (Character.isDigit(fixed[i])
						|| fixed[i] == '.'
						|| fixed[i] == 'x'
						|| fixed[i] == 'y'
						|| fixed[i] == 'r'
						|| fixed[i] == 't'
						|| fixed[i] == 'e'
						|| fixed[i] == 'p') {
						if (iPutOp == 0) {
							postfix[pfxTop++] = fixed[i];
							continue;
						}
						postfix[pfxTop++] = '#';
						postfix[pfxTop++] = fixed[i];
						iPutOp = 0;
					}
					if (fixed[i] == '(') {
						s.push('(');
					}
					if (isoper(fixed[i])) {
						char Op = fixed[i];
						while (precedes(s.peek(), Op) >= 0) {
							postfix[pfxTop++] = s.pop();
						}
						s.push(Op);
						if (Character.isDigit(postfix[pfxTop - 1]))
							iPutOp = 1;
					}
					if (fixed[i] == ')') {
						while (s.peek() != '(') {
							postfix[pfxTop++] = s.pop();
						}
						s.pop();
					}
				}
//			}

			if( s.empty() ) {
				postfix[pfxTop++] = '\0';
				if (!(pfxTop < FMAXLENGTH)) {
					System.err.println(
						"pfxTop < FMAXLENGTH assertion failed in ctop() of teal.math.Function");
					System.exit(1);
				}
		
				for(int k= 0; k<fixed.length; k++ ) {
					fixed[k] = postfix[k];
				}
				System.out.println(this);
				parsed = true;
			} else {
				parsed = false;
			}
			
		} catch( ArrayIndexOutOfBoundsException e ) {
			parsed = false;
		}	
		
	}

	public double evaluateAt(double x) {
		return evaluateAt(x, 0.);
	}

	public double evaluateAt(double x, double y) {
		if( !parsed ) return 0.;
		CharStack s = new CharStack();
		char val;
		int i;
		double n, base;
		DoubleStack s2 = new DoubleStack();
		for (i = 0; fixed[i] != '\0'; i++) {
			val = fixed[i];
			if (val == 'x') {
				s2.push(x);
				continue;
			}
			if (val == 'y') {
				s2.push(y);
				continue;
			}
			if (val == 'r') {
				s2.push(Math.sqrt(x*x+y*y));
				continue;
			}
			if (val == 't') {
				s2.push(Math.atan2(y,x));
				continue;
			}
			if (val == 'e') {
				s2.push(Math.E);
				continue;
			}
			if (val == 'p') {
				s2.push(Math.PI);
				continue;
			}
			if (Character.isDigit(val) || val == '.') {
				s.push(val);
				continue;
			}
			if (!s.empty()) {
				n = 0;
				base = 1;
				while (!s.empty()) {
					if (s.peek() == '.') {
						n /= base;
						base = 1;
						s.pop();
					} else {
						n += (s.pop() - '0') * base;
						base *= 10;
					}
				}
				s2.push(n);
			}
			if (val != '#')
				s2.push(solve(s2.pop(), s2.pop(), val));
		}
		if (s2.empty() && !s.empty()) {
			n = 0;
			base = 1;
			while (!s.empty()) {
				if (s.peek() == '.') {
					n /= base;
					base = 1;
					s.pop();
				} else {
					n += (s.pop() - '0') * base;
					base *= 10;
				}
			}
			s2.push(n);
		}
		return s2.pop();
	}

	boolean isExpressionEnd(char c) {
		return (Character.isDigit(c) || c == '.' || isSymExpressionEnd(c));
	}

	boolean isExpressionStart(char c) {
		return (Character.isDigit(c) || c == '.' || isSymExpressionStart(c));
	}

	boolean isSymExpressionEnd(char c) {
		return (
			c == 'x'
				|| c == 'y'
				|| c == 'r'
				|| c == 't'
				|| c == 'e'
				|| c == 'p'
				|| c == ')');
	}

	boolean isSymExpressionStart(char c) {
		return (
			c == 'x'
				|| c == 'y'
				|| c == 'r'
				|| c == 't'
				|| c == 'e'
				|| c == 'p'
				|| c == '(');
	}

	private void preprocess1() {
		int opC = 0;
		char[] output = new char[FMAXLENGTH];
		for (int i = 0; fixed[i] != 0; i++) {
			// Fixing pi: pi TO p
			if (fixed[i] == 'p' && fixed[i + 1] == 'i') {
				output[opC++] = 'p';
				i += 1;
				continue;
			}
			// Fixing unary minus: - TO (0-1)*
			if (fixed[i] == '-') {
				// Either in the very beginning.
				if (i == 0 ) {
					output[opC++] = '(';
					output[opC++] = '0';
					output[opC++] = '-';
					output[opC++] = '1';
					output[opC++] = ')';
					output[opC++] = '*';
					continue;
				}
				// Or some other possible unary use. (I might have missed cases here.)
				else if ( fixed[i - 1] == '*' || fixed[i - 1] == '/' || fixed[i - 1] == '(') {
					output[opC++] = '(';
					output[opC++] = '0';
					output[opC++] = '-';
					output[opC++] = '1';
					output[opC++] = ')';
					output[opC++] = '*';
					continue;
				}
			}
	
			output[opC++] = fixed[i];
		}
		output[opC] = '\0';

		for(int k= 0; k<fixed.length; k++ ) {
			fixed[k] = output[k];
		}
	}


	
	private void preprocess2() {
		int opC = 0;
		char[] output = new char[FMAXLENGTH];
		for (int i = 0; fixed[i] != 0; i++) {
			// Transforming functions to operations.

			// Hyperbolic
			if (fixed[i] == 's' && fixed[i + 1] == 'i' && fixed[i + 2] == 'n' && fixed[i + 3] == 'h') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'U';
				i += 3;
				continue;
			}
			if (fixed[i] == 'c' && fixed[i + 1] == 'o' && fixed[i + 2] == 's' && fixed[i + 3] == 'h') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'V';
				i += 3;
				continue;
			}
			if (fixed[i] == 't' && fixed[i + 1] == 'a' && fixed[i + 2] == 'n' && fixed[i + 3] == 'h') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'W';
				i += 3;
				continue;
			}
			// Inverse
			if (fixed[i] == 'a' && fixed[i + 1] == 's' && fixed[i + 2] == 'i' && fixed[i + 3] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'X';
				i += 3;
				continue;
			}
			if (fixed[i] == 'a' && fixed[i + 1] == 'c' && fixed[i + 2] == 'o' && fixed[i + 3] == 's') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'Y';
				i += 3;
				continue;
			}
			if (fixed[i] == 'a' && fixed[i + 1] == 't' && fixed[i + 2] == 'a' && fixed[i + 3] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'Z';
				i += 3;
				continue;
			}
			// Trigonometric
			if (fixed[i] == 's' && fixed[i + 1] == 'i' && fixed[i + 2] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'S';
				i += 2;
				continue;
			}
			if (fixed[i] == 'c' && fixed[i + 1] == 'o' && fixed[i + 2] == 's') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'C';
				i += 2;
				continue;
			}
			if (fixed[i] == 't' && fixed[i + 1] == 'a' && fixed[i + 2] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'T';
				i += 2;
				continue;
			}
			// Exponential and Logarithmic
			if (fixed[i] == 'e' && fixed[i + 1] == 'x' && fixed[i + 2] == 'p') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'E';
				i += 2;
				continue;
			}
			if (fixed[i] == 'l' && fixed[i + 1] == 'o' && fixed[i + 2] == 'g') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'L';
				i += 2;
				continue;
			}
			if (fixed[i] == 'l' && fixed[i + 1] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'N';
				i += 1;
				continue;
			}
			// Absolute Value
			if (fixed[i] == 'a' && fixed[i + 1] == 'b' && fixed[i + 2] == 's') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'A';
				i += 2;
				continue;
			}
			// Absolute Value
			if (fixed[i] == 's' && fixed[i + 1] == 'g' && fixed[i + 2] == 'n') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'B';
				i += 2;
				continue;
			}
			// Square Root
			if (fixed[i] == 's' && fixed[i + 1] == 'q' && fixed[i + 2] == 'r' && fixed[i + 3] == 't') {
				if(i!=0) if(isExpressionEnd(fixed[i-1])) output[opC++] = '*';
				output[opC++] = '1';
				output[opC++] = 'Q';
				i += 3;
				continue;
			}
	
			// Fixing expression concatenation: xy TO x*y
			if (isSymExpressionStart(fixed[i]) && i != 0) {
				if (isExpressionEnd(output[opC-1]))
					output[opC++] = '*';
			}
	
			output[opC++] = fixed[i];
		}
		output[opC] = '\0';

		for(int k= 0; k<fixed.length; k++ ) {
			fixed[k] = output[k];
		}
	}

	private double solve(double y, double x, char op) {
		if (op == '+')
			return x + y;
		if (op == '-')
			return x - y;
		if (op == '*')
			return x * y;
		if (op == '/') {
			if (Math.abs(y) < 1e-15)
				y = sign(y) * 1e-15;
			return x / y;
		}
		if (op == '%')
			return (int) x % (int) y;
		if (op == '^') {
			if (Math.abs(y) < 1e-15)
				y = sign(y) * 1e-15;
			if (Math.abs(y) < 1 && x < 0)
				return -1e15;
			else
				return Math.pow(x, y);
		}
			// Hyperbolic
		if (op == 'U')
			return x * ( Math.exp(y) - Math.exp(-y) ) / 2.;
		if (op == 'V')
			return x * ( Math.exp(y) + Math.exp(-y) ) / 2.;
		if (op == 'W')
			return x * ( Math.exp(2.*y) - 1. ) / ( Math.exp(2.*y) + 1. );
			// Inverse
		if (op == 'X')
			return x * Math.asin(y);
		if (op == 'Y')
			return x * Math.acos(y);
		if (op == 'Z')
			return x * Math.atan(y);
			// Trigonometric
		if (op == 'S')
			return x * Math.sin(y);
		if (op == 'C')
			return x * Math.cos(y);
		if (op == 'T')
			return x * Math.tan(y);
			// Exponential and Logarithmic
		if (op == 'E')
			return x * Math.exp(y);
		if (op == 'N')
			return (y <= 0.) ? -1e15 : x * Math.log(y);
		if (op == 'L')
			return (y <= 0.) ? -1e15 : x * Math.log(y) / Math.log(10);
			// Absolute Value
		if (op == 'A')
			return x * Math.abs(y);
			// Signum
		if (op == 'B')
			return x * sgn(y);
			// Square Root
		if (op == 'Q')
			return x * Math.sqrt(Math.abs(y));
		return 0;
	}
	
	public double sgn( double x ) {
		double sign = 0.;
		if( x>=0. ) sign = 1.;
		else if( x<0. ) sign = -1.;
		else sign = 1;

//		double sigmoid = 2.*Math.atan(10.*x)/Math.PI;
		
		return sign;
	}

	public Function(String f) {
		for(int i=0; i<f.length(); i++) {
			raw[i]=f.charAt(i);
			fixed[i]=f.charAt(i);
		}
		for(int j=f.length(); j<raw.length; j++) {
			raw[j]='\0';
			fixed[j]='\0';
		}
		ctop();
	}
	
	
	public Function(Function f)
	{
		for(int i=1; i<raw.length; i++) {
			raw[i]=f.raw[i];
		}
		for(int i=1; i<fixed.length; i++) {
			fixed[i]=f.fixed[i];
		}
	}
	
	
	public String toString() {
		String rawS = cleanStringOf(raw);
		String fixedS = cleanStringOf(fixed);
		return new String("Raw: " + rawS+"\n"+"Fixed: "+fixedS);
	}

	public String cleanStringOf( char [] charArray) {
		String string = new String(charArray);
		int i=0;
		for(; charArray[i]!='\0'; i++);
		return string.substring(0,i);
	}


	private int sign(double x) {
		return (x>0) ? 1 : ( (x<0) ? -1 : 1 );
	}

	private class DoubleStack {
		private double [] stack = new double[FMAXLENGTH];
		private int top = 0;
		public double pop() {if (top>0) return stack[--top]; else return 0;}
		public double peek() {if (top>0) return stack[top-1]; else return 0;}
		public void push(double a) {if (top<100) stack[top++]=a;}
		public boolean empty() {return (top==0);}
	}

	private class CharStack {
		private char [] stack = new char[FMAXLENGTH];
		private int top = 0;
		public char pop() {if (top>0) return stack[--top]; else return 0;}
		public char peek() {if (top>0) return stack[top-1]; else return 0;}
		public void push(char a) {if (top<100) stack[top++]=a;}
		public boolean empty() {return (top==0);}
	}

}






