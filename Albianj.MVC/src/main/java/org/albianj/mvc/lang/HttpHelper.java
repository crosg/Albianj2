
package org.albianj.mvc.lang;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by xuhaifeng on 16/10/20.
 */
public class HttpHelper {
	
	
	
	/**
	 * The array of escaped HTML character values, indexed on char value.
	 * <p/>
	 * HTML entities values were derived from Jakarta Commons Lang
	 * <tt>org.apache.commons.lang.Entities</tt> class.
	 */
	private static final String[ ] HTML_ENTITIES = new String[ 9999 ];
	/**
	 * The array of escaped XML character values, indexed on char value.
	 * <p/>
	 * XML entities values were derived from Jakarta Commons Lang
	 * <tt>org.apache.commons.lang.Entities</tt> class.
	 */
	private static final String[ ] XML_ENTITIES = new String[ 63 ];


	static {
		HTML_ENTITIES[ 34 ] = "&quot;"; // " - double-quote
		HTML_ENTITIES[ 38 ] = "&amp;"; // & - ampersand
		HTML_ENTITIES[ 60 ] = "&lt;"; // < - less-than
		HTML_ENTITIES[ 62 ] = "&gt;"; // > - greater-than
		HTML_ENTITIES[ 160 ] = "&nbsp;"; // non-breaking space
		HTML_ENTITIES[ 161 ] = "&iexcl;"; // inverted exclamation mark
		HTML_ENTITIES[ 162 ] = "&cent;";  // cent sign
		HTML_ENTITIES[ 163 ] = "&pound;"; // pound sign
		HTML_ENTITIES[ 164 ] = "&curren;"; // currency sign
		HTML_ENTITIES[ 165 ] = "&yen;";  // yen sign = yuan sign
		HTML_ENTITIES[ 166 ] = "&brvbar;"; // broken bar = broken
		                                   // vertical bar
		HTML_ENTITIES[ 167 ] = "&sect;"; // section sign
		HTML_ENTITIES[ 168 ] = "&uml;"; // diaeresis = spacing diaeresis
		HTML_ENTITIES[ 169 ] = "&copy;"; // © - copyright sign
		HTML_ENTITIES[ 170 ] = "&ordf;"; // feminine ordinal indicator
		HTML_ENTITIES[ 171 ] = "&laquo;"; // left-pointing double angle
		                                  // quotation mark = left
		                                  // pointing guillemet
		HTML_ENTITIES[ 172 ] = "&not;";   // not sign
		HTML_ENTITIES[ 173 ] = "&shy;";   // soft hyphen = discretionary
		                                  // hyphen
		HTML_ENTITIES[ 174 ] = "&reg;";   // ® - registered trademark
		                                  // sign
		HTML_ENTITIES[ 175 ] = "&macr;";   // macron = spacing macron =
		                                   // overline = APL overbar
		HTML_ENTITIES[ 176 ] = "&deg;";   // degree sign
		HTML_ENTITIES[ 177 ] = "&plusmn;";   // plus-minus sign =
		                                     // plus-or-minus sign
		HTML_ENTITIES[ 178 ] = "&sup2;";   // superscript two =
		                                   // superscript digit two =
		                                   // squared
		HTML_ENTITIES[ 179 ] = "&sup3;";   // superscript three =
		                                   // superscript digit three =
		                                   // cubed
		HTML_ENTITIES[ 180 ] = "&acute;";   // acute accent = spacing
		                                    // acute
		HTML_ENTITIES[ 181 ] = "&micro;";   // micro sign
		HTML_ENTITIES[ 182 ] = "&para;";   // pilcrow sign = paragraph
		                                   // sign
		HTML_ENTITIES[ 183 ] = "&middot;";   // middle dot = Georgian
		                                     // comma = Greek middle dot
		HTML_ENTITIES[ 184 ] = "&cedil;";   // cedilla = spacing cedilla
		HTML_ENTITIES[ 185 ] = "&sup1;";   // superscript one =
		                                   // superscript digit one
		HTML_ENTITIES[ 186 ] = "&ordm;";   // masculine ordinal
		                                   // indicator
		HTML_ENTITIES[ 187 ] = "&raquo;";   // right-pointing double
		                                    // angle quotation mark =
		                                    // right pointing guillemet
		HTML_ENTITIES[ 188 ] = "&frac14;";   // vulgar fraction one
		                                     // quarter = fraction one
		                                     // quarter
		HTML_ENTITIES[ 189 ] = "&frac12;";   // vulgar fraction one half
		                                     // = fraction one half
		HTML_ENTITIES[ 190 ] = "&frac34;";   // vulgar fraction three
		                                     // quarters = fraction
		                                     // three quarters
		HTML_ENTITIES[ 191 ] = "&iquest;";   // inverted question mark =
		                                     // turned question mark
		HTML_ENTITIES[ 192 ] = "&Agrave;";   // À - uppercase A, grave
		                                     // accent
		HTML_ENTITIES[ 193 ] = "&Aacute;";   // Á - uppercase A, acute
		                                     // accent
		HTML_ENTITIES[ 194 ] = "&Acirc;";   // Â - uppercase A,
		                                    // circumflex accent
		HTML_ENTITIES[ 195 ] = "&Atilde;";   // Ã - uppercase A, tilde
		HTML_ENTITIES[ 196 ] = "&Auml;";   // Ä - uppercase A, umlaut
		HTML_ENTITIES[ 197 ] = "&Aring;";   // Å - uppercase A, ring
		HTML_ENTITIES[ 198 ] = "&AElig;";   // Æ - uppercase AE
		HTML_ENTITIES[ 199 ] = "&Ccedil;";   // Ç - uppercase C, cedilla
		HTML_ENTITIES[ 200 ] = "&Egrave;";   // È - uppercase E, grave
		                                     // accent
		HTML_ENTITIES[ 201 ] = "&Eacute;";   // É - uppercase E, acute
		                                     // accent
		HTML_ENTITIES[ 202 ] = "&Ecirc;";   // Ê - uppercase E,
		                                    // circumflex accent
		HTML_ENTITIES[ 203 ] = "&Euml;";   // Ë - uppercase E, umlaut
		HTML_ENTITIES[ 204 ] = "&Igrave;";   // Ì - uppercase I, grave
		                                     // accent
		HTML_ENTITIES[ 205 ] = "&Iacute;";   // Í - uppercase I, acute
		                                     // accent
		HTML_ENTITIES[ 206 ] = "&Icirc;";   // Î - uppercase I,
		                                    // circumflex accent
		HTML_ENTITIES[ 207 ] = "&Iuml;";   // Ï - uppercase I, umlaut
		HTML_ENTITIES[ 208 ] = "&ETH;";   // Ð - uppercase Eth,
		                                  // Icelandic
		HTML_ENTITIES[ 209 ] = "&Ntilde;";   // Ñ - uppercase N, tilde
		HTML_ENTITIES[ 210 ] = "&Ograve;";   // Ò - uppercase O, grave
		                                     // accent
		HTML_ENTITIES[ 211 ] = "&Oacute;";   // Ó - uppercase O, acute
		                                     // accent
		HTML_ENTITIES[ 212 ] = "&Ocirc;";   // Ô - uppercase O,
		                                    // circumflex accent
		HTML_ENTITIES[ 213 ] = "&Otilde;";   // Õ - uppercase O, tilde
		HTML_ENTITIES[ 214 ] = "&Ouml;";   // Ö - uppercase O, umlaut
		HTML_ENTITIES[ 215 ] = "&times;";   // multiplication sign
		HTML_ENTITIES[ 216 ] = "&Oslash;";   // Ø - uppercase O, slash
		HTML_ENTITIES[ 217 ] = "&Ugrave;";   // Ù - uppercase U, grave
		                                     // accent
		HTML_ENTITIES[ 218 ] = "&Uacute;";   // Ú - uppercase U, acute
		                                     // accent
		HTML_ENTITIES[ 219 ] = "&Ucirc;";   // Û - uppercase U,
		                                    // circumflex accent
		HTML_ENTITIES[ 220 ] = "&Uuml;";   // Ü - uppercase U, umlaut
		HTML_ENTITIES[ 221 ] = "&Yacute;";   // Ý - uppercase Y, acute
		                                     // accent
		HTML_ENTITIES[ 222 ] = "&THORN;";   // Þ - uppercase THORN,
		                                    // Icelandic
		HTML_ENTITIES[ 223 ] = "&szlig;";   // ß - lowercase sharps,
		                                    // German
		HTML_ENTITIES[ 224 ] = "&agrave;";   // à - lowercase a, grave
		                                     // accent
		HTML_ENTITIES[ 225 ] = "&aacute;";   // á - lowercase a, acute
		                                     // accent
		HTML_ENTITIES[ 226 ] = "&acirc;";   // â - lowercase a,
		                                    // circumflex accent
		HTML_ENTITIES[ 227 ] = "&atilde;";   // ã - lowercase a, tilde
		HTML_ENTITIES[ 228 ] = "&auml;";   // ä - lowercase a, umlaut
		HTML_ENTITIES[ 229 ] = "&aring;";   // å - lowercase a, ring
		HTML_ENTITIES[ 230 ] = "&aelig;";   // æ - lowercase ae
		HTML_ENTITIES[ 231 ] = "&ccedil;";   // ç - lowercase c, cedilla
		HTML_ENTITIES[ 232 ] = "&egrave;";   // è - lowercase e, grave
		                                     // accent
		HTML_ENTITIES[ 233 ] = "&eacute;";   // é - lowercase e, acute
		                                     // accent
		HTML_ENTITIES[ 234 ] = "&ecirc;";   // ê - lowercase e,
		                                    // circumflex accent
		HTML_ENTITIES[ 235 ] = "&euml;";   // ë - lowercase e, umlaut
		HTML_ENTITIES[ 236 ] = "&igrave;";   // ì - lowercase i, grave
		                                     // accent
		HTML_ENTITIES[ 237 ] = "&iacute;";   // í - lowercase i, acute
		                                     // accent
		HTML_ENTITIES[ 238 ] = "&icirc;";   // î - lowercase i,
		                                    // circumflex accent
		HTML_ENTITIES[ 239 ] = "&iuml;";   // ï - lowercase i, umlaut
		HTML_ENTITIES[ 240 ] = "&eth;";   // ð - lowercase eth,
		                                  // Icelandic
		HTML_ENTITIES[ 241 ] = "&ntilde;";   // ñ - lowercase n, tilde
		HTML_ENTITIES[ 242 ] = "&ograve;";   // ò - lowercase o, grave
		                                     // accent
		HTML_ENTITIES[ 243 ] = "&oacute;";   // ó - lowercase o, acute
		                                     // accent
		HTML_ENTITIES[ 244 ] = "&ocirc;";   // ô - lowercase o,
		                                    // circumflex accent
		HTML_ENTITIES[ 245 ] = "&otilde;";   // õ - lowercase o, tilde
		HTML_ENTITIES[ 246 ] = "&ouml;";   // ö - lowercase o, umlaut
		HTML_ENTITIES[ 247 ] = "&divide;";   // division sign
		HTML_ENTITIES[ 248 ] = "&oslash;";   // ø - lowercase o, slash
		HTML_ENTITIES[ 249 ] = "&ugrave;";   // ù - lowercase u, grave
		                                     // accent
		HTML_ENTITIES[ 250 ] = "&uacute;";   // ú - lowercase u, acute
		                                     // accent
		HTML_ENTITIES[ 251 ] = "&ucirc;";   // û - lowercase u,
		                                    // circumflex accent
		HTML_ENTITIES[ 252 ] = "&uuml;";   // ü - lowercase u, umlaut
		HTML_ENTITIES[ 253 ] = "&yacute;";   // ý - lowercase y, acute
		                                     // accent
		HTML_ENTITIES[ 254 ] = "&thorn;";   // þ - lowercase thorn,
		                                    // Icelandic
		HTML_ENTITIES[ 255 ] = "&yuml;";   // ÿ - lowercase y, umlaut
		// http://www.w3.org/TR/REC-html40/sgml/entities.html
		// <!-- Latin Extended-B -->
		HTML_ENTITIES[ 402 ] = "&fnof;";   // latin small f with hook =
		                                   // function= florin, U+0192
		                                   // ISOtech -->
		// <!-- Greek -->
		HTML_ENTITIES[ 913 ] = "&Alpha;";   // greek capital letter
		                                    // alpha, U+0391 -->
		HTML_ENTITIES[ 914 ] = "&Beta;";   // greek capital letter beta,
		                                   // U+0392 -->
		HTML_ENTITIES[ 915 ] = "&Gamma;";   // greek capital letter
		                                    // gamma,U+0393 ISOgrk3 -->
		HTML_ENTITIES[ 916 ] = "&Delta;";   // greek capital letter
		                                    // delta,U+0394 ISOgrk3 -->
		HTML_ENTITIES[ 917 ] = "&Epsilon;";   // greek capital letter
		                                      // epsilon, U+0395 -->
		HTML_ENTITIES[ 918 ] = "&Zeta;";   // greek capital letter zeta,
		                                   // U+0396 -->
		HTML_ENTITIES[ 919 ] = "&Eta;";   // greek capital letter eta,
		                                  // U+0397 -->
		HTML_ENTITIES[ 920 ] = "&Theta;";   // greek capital letter
		                                    // theta,U+0398 ISOgrk3 -->
		HTML_ENTITIES[ 921 ] = "&Iota;";   // greek capital letter iota,
		                                   // U+0399 -->
		HTML_ENTITIES[ 922 ] = "&Kappa;";   // greek capital letter
		                                    // kappa, U+039A -->
		HTML_ENTITIES[ 923 ] = "&Lambda;";   // greek capital letter
		                                     // lambda,U+039B ISOgrk3
		                                     // -->
		HTML_ENTITIES[ 924 ] = "&Mu;";   // greek capital letter mu,
		                                 // U+039C -->
		HTML_ENTITIES[ 925 ] = "&Nu;";   // greek capital letter nu,
		                                 // U+039D -->
		HTML_ENTITIES[ 926 ] = "&Xi;";   // greek capital letter xi,
		                                 // U+039E ISOgrk3 -->
		HTML_ENTITIES[ 927 ] = "&Omicron;";   // greek capital letter
		                                      // omicron, U+039F -->
		HTML_ENTITIES[ 928 ] = "&Pi;";   // greek capital letter pi,
		                                 // U+03A0 ISOgrk3 -->
		HTML_ENTITIES[ 929 ] = "&Rho;";   // greek capital letter rho,
		                                  // U+03A1 -->
		// <!-- there is no Sigmaf, and no U+03A2 character either -->
		HTML_ENTITIES[ 931 ] = "&Sigma;";   // greek capital letter
		                                    // sigma,U+03A3 ISOgrk3 -->
		HTML_ENTITIES[ 932 ] = "&Tau;";   // greek capital letter tau,
		                                  // U+03A4 -->
		HTML_ENTITIES[ 933 ] = "&Upsilon;";   // greek capital letter
		                                      // upsilon,U+03A5 ISOgrk3
		                                      // -->
		HTML_ENTITIES[ 934 ] = "&Phi;";   // greek capital letter
		                                  // phi,U+03A6 ISOgrk3 -->
		HTML_ENTITIES[ 935 ] = "&Chi;";   // greek capital letter chi,
		                                  // U+03A7 -->
		HTML_ENTITIES[ 936 ] = "&Psi;";   // greek capital letter
		                                  // psi,U+03A8 ISOgrk3 -->
		HTML_ENTITIES[ 937 ] = "&Omega;";   // greek capital letter
		                                    // omega,U+03A9 ISOgrk3 -->
		HTML_ENTITIES[ 945 ] = "&alpha;";   // greek small letter
		                                    // alpha,U+03B1 ISOgrk3 -->
		HTML_ENTITIES[ 946 ] = "&beta;";   // greek small letter beta,
		                                   // U+03B2 ISOgrk3 -->
		HTML_ENTITIES[ 947 ] = "&gamma;";   // greek small letter
		                                    // gamma,U+03B3 ISOgrk3 -->
		HTML_ENTITIES[ 948 ] = "&delta;";   // greek small letter
		                                    // delta,U+03B4 ISOgrk3 -->
		HTML_ENTITIES[ 949 ] = "&epsilon;";   // greek small letter
		                                      // epsilon,U+03B5 ISOgrk3
		                                      // -->
		HTML_ENTITIES[ 950 ] = "&zeta;";   // greek small letter zeta,
		                                   // U+03B6 ISOgrk3 -->
		HTML_ENTITIES[ 951 ] = "&eta;";   // greek small letter eta,
		                                  // U+03B7 ISOgrk3 -->
		HTML_ENTITIES[ 952 ] = "&theta;";   // greek small letter
		                                    // theta,U+03B8 ISOgrk3 -->
		HTML_ENTITIES[ 953 ] = "&iota;";   // greek small letter iota,
		                                   // U+03B9 ISOgrk3 -->
		HTML_ENTITIES[ 954 ] = "&kappa;";   // greek small letter
		                                    // kappa,U+03BA ISOgrk3 -->
		HTML_ENTITIES[ 955 ] = "&lambda;";   // greek small letter
		                                     // lambda,U+03BB ISOgrk3
		                                     // -->
		HTML_ENTITIES[ 956 ] = "&mu;";   // greek small letter mu,
		                                 // U+03BC ISOgrk3 -->
		HTML_ENTITIES[ 957 ] = "&nu;";   // greek small letter nu,
		                                 // U+03BD ISOgrk3 -->
		HTML_ENTITIES[ 958 ] = "&xi;";   // greek small letter xi,
		                                 // U+03BE ISOgrk3 -->
		HTML_ENTITIES[ 959 ] = "&omicron;";   // greek small letter
		                                      // omicron, U+03BF NEW -->
		HTML_ENTITIES[ 960 ] = "&pi;";   // greek small letter pi,
		                                 // U+03C0 ISOgrk3 -->
		HTML_ENTITIES[ 961 ] = "&rho;";   // greek small letter rho,
		                                  // U+03C1 ISOgrk3 -->
		HTML_ENTITIES[ 962 ] = "&sigmaf;";   // greek small letter final
		                                     // sigma,U+03C2 ISOgrk3 -->
		HTML_ENTITIES[ 963 ] = "&sigma;";   // greek small letter
		                                    // sigma,U+03C3 ISOgrk3 -->
		HTML_ENTITIES[ 964 ] = "&tau;";   // greek small letter tau,
		                                  // U+03C4 ISOgrk3 -->
		HTML_ENTITIES[ 965 ] = "&upsilon;";   // greek small letter
		                                      // upsilon,U+03C5 ISOgrk3
		                                      // -->
		HTML_ENTITIES[ 966 ] = "&phi;";   // greek small letter phi,
		                                  // U+03C6 ISOgrk3 -->
		HTML_ENTITIES[ 967 ] = "&chi;";   // greek small letter chi,
		                                  // U+03C7 ISOgrk3 -->
		HTML_ENTITIES[ 968 ] = "&psi;";   // greek small letter psi,
		                                  // U+03C8 ISOgrk3 -->
		HTML_ENTITIES[ 969 ] = "&omega;";   // greek small letter
		                                    // omega,U+03C9 ISOgrk3 -->
		HTML_ENTITIES[ 977 ] = "&thetasym;";   // greek small letter
		                                       // theta symbol,U+03D1
		                                       // NEW -->
		HTML_ENTITIES[ 978 ] = "&upsih;";   // greek upsilon with hook
		                                    // symbol,U+03D2 NEW -->
		HTML_ENTITIES[ 982 ] = "&piv;";   // greek pi symbol, U+03D6
		                                  // ISOgrk3 -->
		// <!-- General Punctuation -->
		HTML_ENTITIES[ 8226 ] = "&bull;";   // bullet = black small
		                                    // circle,U+2022 ISOpub -->
		// <!-- bullet is NOT the same as bullet operator, U+2219 -->
		HTML_ENTITIES[ 8230 ] = "&hellip;";   // horizontal ellipsis =
		                                      // three dot leader,U+2026
		                                      // ISOpub -->
		HTML_ENTITIES[ 8242 ] = "&prime;";   // prime = minutes = feet,
		                                     // U+2032 ISOtech -->
		HTML_ENTITIES[ 8243 ] = "&Prime;";   // double prime = seconds =
		                                     // inches,U+2033 ISOtech
		                                     // -->
		HTML_ENTITIES[ 8254 ] = "&oline;";   // overline = spacing
		                                     // overscore,U+203E NEW -->
		HTML_ENTITIES[ 8260 ] = "&frasl;";   // fraction slash, U+2044
		                                     // NEW -->
		// <!-- Letterlike Symbols -->
		HTML_ENTITIES[ 8472 ] = "&weierp;";   // script capital P =
		                                      // power set= Weierstrass
		                                      // p, U+2118 ISOamso -->
		HTML_ENTITIES[ 8465 ] = "&image;";   // blackletter capital I =
		                                     // imaginary part,U+2111
		                                     // ISOamso -->
		HTML_ENTITIES[ 8476 ] = "&real;";   // blackletter capital R =
		                                    // real part symbol,U+211C
		                                    // ISOamso -->
		HTML_ENTITIES[ 8482 ] = "&trade;";   // trade mark sign, U+2122
		                                     // ISOnum -->
		HTML_ENTITIES[ 8501 ] = "&alefsym;";   // alef symbol = first
		                                       // transfinite
		                                       // cardinal,U+2135 NEW
		                                       // -->
		// <!-- alef symbol is NOT the same as hebrew letter alef,U+05D0
		// although the same glyph could be used to depict both
		// characters -->
		// <!-- Arrows -->
		HTML_ENTITIES[ 8592 ] = "&larr;";   // leftwards arrow, U+2190
		                                    // ISOnum -->
		HTML_ENTITIES[ 8593 ] = "&uarr;";   // upwards arrow, U+2191
		                                    // ISOnum-->
		HTML_ENTITIES[ 8594 ] = "&rarr;";   // rightwards arrow, U+2192
		                                    // ISOnum -->
		HTML_ENTITIES[ 8595 ] = "&darr;";   // downwards arrow, U+2193
		                                    // ISOnum -->
		HTML_ENTITIES[ 8596 ] = "&harr;";   // left right arrow, U+2194
		                                    // ISOamsa -->
		HTML_ENTITIES[ 8629 ] = "&crarr;";   // downwards arrow with
		                                     // corner leftwards=
		                                     // carriage return, U+21B5
		                                     // NEW -->
		HTML_ENTITIES[ 8656 ] = "&lArr;";   // leftwards double arrow,
		                                    // U+21D0 ISOtech -->
		// <!-- ISO 10646 does not say that lArr is the same as the 'is
		// implied by' arrowbut also does not have any other character
		// for that function. So ? lArr canbe used for 'is implied by'
		// as ISOtech suggests -->
		HTML_ENTITIES[ 8657 ] = "&uArr;";   // upwards double arrow,
		                                    // U+21D1 ISOamsa -->
		HTML_ENTITIES[ 8658 ] = "&rArr;";   // rightwards double
		                                    // arrow,U+21D2 ISOtech -->
		// <!-- ISO 10646 does not say this is the 'implies' character
		// but does not have another character with this function so
		// ?rArr can be used for 'implies' as ISOtech suggests -->
		HTML_ENTITIES[ 8659 ] = "&dArr;";   // downwards double arrow,
		                                    // U+21D3 ISOamsa -->
		HTML_ENTITIES[ 8660 ] = "&hArr;";   // left right double
		                                    // arrow,U+21D4 ISOamsa -->
		// <!-- Mathematical Operators -->
		HTML_ENTITIES[ 8704 ] = "&forall;";   // for all, U+2200 ISOtech
		                                      // -->
		HTML_ENTITIES[ 8706 ] = "&part;";   // partial differential,
		                                    // U+2202 ISOtech -->
		HTML_ENTITIES[ 8707 ] = "&exist;";   // there exists, U+2203
		                                     // ISOtech -->
		HTML_ENTITIES[ 8709 ] = "&empty;";   // empty set = null set =
		                                     // diameter,U+2205 ISOamso
		                                     // -->
		HTML_ENTITIES[ 8711 ] = "&nabla;";   // nabla = backward
		                                     // difference,U+2207
		                                     // ISOtech -->
		HTML_ENTITIES[ 8712 ] = "&isin;";   // element of, U+2208
		                                    // ISOtech -->
		HTML_ENTITIES[ 8713 ] = "&notin;";   // not an element of,
		                                     // U+2209 ISOtech -->
		HTML_ENTITIES[ 8715 ] = "&ni;";   // contains as member, U+220B
		                                  // ISOtech -->
		// <!-- should there be a more memorable name than 'ni'? -->
		HTML_ENTITIES[ 8719 ] = "&prod;";   // n-ary product = product
		                                    // sign,U+220F ISOamsb -->
		// <!-- prod is NOT the same character as U+03A0 'greek capital
		// letter pi' though the same glyph might be used for both -->
		HTML_ENTITIES[ 8721 ] = "&sum;";   // n-ary summation, U+2211
		                                   // ISOamsb -->
		// <!-- sum is NOT the same character as U+03A3 'greek capital
		// letter sigma' though the same glyph might be used for both
		// -->
		HTML_ENTITIES[ 8722 ] = "&minus;";   // minus sign, U+2212
		                                     // ISOtech -->
		HTML_ENTITIES[ 8727 ] = "&lowast;";   // asterisk operator,
		                                      // U+2217 ISOtech -->
		HTML_ENTITIES[ 8730 ] = "&radic;";   // square root = radical
		                                     // sign,U+221A ISOtech -->
		HTML_ENTITIES[ 8733 ] = "&prop;";   // proportional to, U+221D
		                                    // ISOtech -->
		HTML_ENTITIES[ 8734 ] = "&infin;";   // infinity, U+221E ISOtech
		                                     // -->
		HTML_ENTITIES[ 8736 ] = "&ang;";   // angle, U+2220 ISOamso -->
		HTML_ENTITIES[ 8743 ] = "&and;";   // logical and = wedge,
		                                   // U+2227 ISOtech -->
		HTML_ENTITIES[ 8744 ] = "&or;";   // logical or = vee, U+2228
		                                  // ISOtech -->
		HTML_ENTITIES[ 8745 ] = "&cap;";   // intersection = cap, U+2229
		                                   // ISOtech -->
		HTML_ENTITIES[ 8746 ] = "&cup;";   // union = cup, U+222A
		                                   // ISOtech -->
		HTML_ENTITIES[ 8747 ] = "&int;";   // integral, U+222B ISOtech
		                                   // -->
		HTML_ENTITIES[ 8756 ] = "&there4;";   // therefore, U+2234
		                                      // ISOtech -->
		HTML_ENTITIES[ 8764 ] = "&sim;";   // tilde operator = varies
		                                   // with = similar to,U+223C
		                                   // ISOtech -->
		// <!-- tilde operator is NOT the same character as the tilde,
		// U+007E,although the same glyph might be used to represent
		// both -->
		HTML_ENTITIES[ 8773 ] = "&cong;";   // approximately equal to,
		                                    // U+2245 ISOtech -->
		HTML_ENTITIES[ 8776 ] = "&asymp;";   // almost equal to =
		                                     // asymptotic to,U+2248
		                                     // ISOamsr -->
		HTML_ENTITIES[ 8800 ] = "&ne;";   // not equal to, U+2260
		                                  // ISOtech -->
		HTML_ENTITIES[ 8801 ] = "&equiv;";   // identical to, U+2261
		                                     // ISOtech -->
		HTML_ENTITIES[ 8804 ] = "&le;";   // less-than or equal to,
		                                  // U+2264 ISOtech -->
		HTML_ENTITIES[ 8805 ] = "&ge;";   // greater-than or equal
		                                  // to,U+2265 ISOtech -->
		HTML_ENTITIES[ 8834 ] = "&sub;";   // subset of, U+2282 ISOtech
		                                   // -->
		HTML_ENTITIES[ 8835 ] = "&sup;";   // superset of, U+2283
		                                   // ISOtech -->
		// <!-- note that nsup, 'not a superset of, U+2283' is not
		// covered by the Symbol font encoding and is not included.
		// Should it be, for symmetry?It is in ISOamsn --> <!ENTITY
		// nsub"; 8836 //not a subset of, U+2284 ISOamsn -->
		HTML_ENTITIES[ 8838 ] = "&sube;";   // subset of or equal to,
		                                    // U+2286 ISOtech -->
		HTML_ENTITIES[ 8839 ] = "&supe;";   // superset of or equal
		                                    // to,U+2287 ISOtech -->
		HTML_ENTITIES[ 8853 ] = "&oplus;";   // circled plus = direct
		                                     // sum,U+2295 ISOamsb -->
		HTML_ENTITIES[ 8855 ] = "&otimes;";   // circled times = vector
		                                      // product,U+2297 ISOamsb
		                                      // -->
		HTML_ENTITIES[ 8869 ] = "&perp;";   // up tack = orthogonal to =
		                                    // perpendicular,U+22A5
		                                    // ISOtech -->
		HTML_ENTITIES[ 8901 ] = "&sdot;";   // dot operator, U+22C5
		                                    // ISOamsb -->
		// <!-- dot operator is NOT the same character as U+00B7 middle
		// dot -->
		// <!-- Miscellaneous Technical -->
		HTML_ENTITIES[ 8968 ] = "&lceil;";   // left ceiling = apl
		                                     // upstile,U+2308 ISOamsc
		                                     // -->
		HTML_ENTITIES[ 8969 ] = "&rceil;";   // right ceiling, U+2309
		                                     // ISOamsc -->
		HTML_ENTITIES[ 8970 ] = "&lfloor;";   // left floor = apl
		                                      // downstile,U+230A
		                                      // ISOamsc -->
		HTML_ENTITIES[ 8971 ] = "&rfloor;";   // right floor, U+230B
		                                      // ISOamsc -->
		HTML_ENTITIES[ 9001 ] = "&lang;";   // left-pointing angle
		                                    // bracket = bra,U+2329
		                                    // ISOtech -->
		// <!-- lang is NOT the same character as U+003C 'less than' or
		// U+2039 'single left-pointing angle quotation mark' -->
		HTML_ENTITIES[ 9002 ] = "&rang;";   // right-pointing angle
		                                    // bracket = ket,U+232A
		                                    // ISOtech -->
		// <!-- rang is NOT the same character as U+003E 'greater than'
		// or U+203A 'single right-pointing angle quotation mark' -->
		// <!-- Geometric Shapes -->
		HTML_ENTITIES[ 9674 ] = "&loz;";   // lozenge, U+25CA ISOpub -->
		// <!-- Miscellaneous Symbols -->
		HTML_ENTITIES[ 9824 ] = "&spades;";   // black spade suit,
		                                      // U+2660 ISOpub -->
		// <!-- black here seems to mean filled as opposed to hollow -->
		HTML_ENTITIES[ 9827 ] = "&clubs;";   // black club suit =
		                                     // shamrock,U+2663 ISOpub
		                                     // -->
		HTML_ENTITIES[ 9829 ] = "&hearts;";   // black heart suit =
		                                      // valentine,U+2665 ISOpub
		                                      // -->
		HTML_ENTITIES[ 9830 ] = "&diams;";   // black diamond suit,
		                                     // U+2666 ISOpub -->
		// <!-- Latin Extended-A -->
		HTML_ENTITIES[ 338 ] = "&OElig;";   // -- latin capital ligature
		                                    // OE,U+0152 ISOlat2 -->
		HTML_ENTITIES[ 339 ] = "&oelig;";   // -- latin small ligature
		                                    // oe, U+0153 ISOlat2 -->
		// <!-- ligature is a misnomer, this is a separate character in
		// some languages -->
		HTML_ENTITIES[ 352 ] = "&Scaron;";   // -- latin capital letter
		                                     // S with caron,U+0160
		                                     // ISOlat2 -->
		HTML_ENTITIES[ 353 ] = "&scaron;";   // -- latin small letter s
		                                     // with caron,U+0161
		                                     // ISOlat2 -->
		HTML_ENTITIES[ 376 ] = "&Yuml;";   // -- latin capital letter Y
		                                   // with diaeresis,U+0178
		                                   // ISOlat2 -->
		// <!-- Spacing Modifier Letters -->
		HTML_ENTITIES[ 710 ] = "&circ;";   // -- modifier letter
		                                   // circumflex accent,U+02C6
		                                   // ISOpub -->
		HTML_ENTITIES[ 732 ] = "&tilde;";   // small tilde, U+02DC
		                                    // ISOdia -->
		// <!-- General Punctuation -->
		HTML_ENTITIES[ 8194 ] = "&ensp;";   // en space, U+2002 ISOpub
		                                    // -->
		HTML_ENTITIES[ 8195 ] = "&emsp;";   // em space, U+2003 ISOpub
		                                    // -->
		HTML_ENTITIES[ 8201 ] = "&thinsp;";   // thin space, U+2009
		                                      // ISOpub -->
		HTML_ENTITIES[ 8204 ] = "&zwnj;";   // zero width
		                                    // non-joiner,U+200C NEW RFC
		                                    // 2070 -->
		HTML_ENTITIES[ 8205 ] = "&zwj;";   // zero width joiner, U+200D
		                                   // NEW RFC 2070 -->
		HTML_ENTITIES[ 8206 ] = "&lrm;";   // left-to-right mark, U+200E
		                                   // NEW RFC 2070 -->
		HTML_ENTITIES[ 8207 ] = "&rlm;";   // right-to-left mark, U+200F
		                                   // NEW RFC 2070 -->
		HTML_ENTITIES[ 8211 ] = "&ndash;";   // en dash, U+2013 ISOpub
		                                     // -->
		HTML_ENTITIES[ 8212 ] = "&mdash;";   // em dash, U+2014 ISOpub
		                                     // -->
		HTML_ENTITIES[ 8216 ] = "&lsquo;";   // left single quotation
		                                     // mark,U+2018 ISOnum -->
		HTML_ENTITIES[ 8217 ] = "&rsquo;";   // right single quotation
		                                     // mark,U+2019 ISOnum -->
		HTML_ENTITIES[ 8218 ] = "&sbquo;";   // single low-9 quotation
		                                     // mark, U+201A NEW -->
		HTML_ENTITIES[ 8220 ] = "&ldquo;";   // left double quotation
		                                     // mark,U+201C ISOnum -->
		HTML_ENTITIES[ 8221 ] = "&rdquo;";   // right double quotation
		                                     // mark,U+201D ISOnum -->
		HTML_ENTITIES[ 8222 ] = "&bdquo;";   // double low-9 quotation
		                                     // mark, U+201E NEW -->
		HTML_ENTITIES[ 8224 ] = "&dagger;";   // dagger, U+2020 ISOpub
		                                      // -->
		HTML_ENTITIES[ 8225 ] = "&Dagger;";   // double dagger, U+2021
		                                      // ISOpub -->
		HTML_ENTITIES[ 8240 ] = "&permil;";   // per mille sign, U+2030
		                                      // ISOtech -->
		HTML_ENTITIES[ 8249 ] = "&lsaquo;";   // single left-pointing
		                                      // angle quotation
		                                      // mark,U+2039 ISO
		                                      // proposed -->
		// <!-- lsaquo is proposed but not yet ISO standardized -->
		HTML_ENTITIES[ 8250 ] = "&rsaquo;";   // single right-pointing
		                                      // angle quotation
		                                      // mark,U+203A ISO
		                                      // proposed -->
		// <!-- rsaquo is proposed but not yet ISO standardized -->
		HTML_ENTITIES[ 8364 ] = "&euro;";   // -- euro sign, U+20AC NEW
		                                    // -->
	}

	static {
		XML_ENTITIES[ 34 ] = "&quot;"; // " - double-quote
		XML_ENTITIES[ 38 ] = "&amp;"; // & - ampersand
		XML_ENTITIES[ 39 ] = "&#039;"; // ' - quote
		XML_ENTITIES[ 60 ] = "&lt;"; // < - less-than
		XML_ENTITIES[ 62 ] = "&gt;"; // > - greater-than
	}

	/**
	 * Append the escaped string for the given character value to the
	 * buffer. The following characters are escaped: &lt;, &gt;, &quot;,
	 * &#039;, &amp;.
	 *
	 * @param aChar
	 *                the character value to escape
	 * @param buffer
	 *                the string buffer to append the escaped value to
	 */
	static void appendEscapeChar( char aChar, HtmlStringBuffer buffer ) {
		int index = aChar;

		if ( index < XML_ENTITIES.length && XML_ENTITIES[ index ] != null ) {
			buffer.append( XML_ENTITIES[ index ] );

		} else {
			buffer.append( aChar );
		}
	}

	/**
	 * Append the escaped string for the given character value to the
	 * buffer. The following characters are escaped: &lt;, &gt;, &quot;,
	 * &#039;, &amp;.
	 *
	 * @param value
	 *                the String value to escape
	 * @param buffer
	 *                the string buffer to append the escaped value to
	 */
	static void appendEscapeString( String value, HtmlStringBuffer buffer ) {
		char aChar;
		for ( int i = 0, size = value.length( ); i < size; i++ ) {
			aChar = value.charAt( i );
			int index = aChar;

			if ( index < XML_ENTITIES.length && XML_ENTITIES[ index ] != null ) {
				buffer.append( XML_ENTITIES[ index ] );

			} else {
				buffer.append( aChar );
			}
		}
	}

	/**
	 * Append the HTML escaped string for the given character value to the
	 * buffer.
	 *
	 * @param aChar
	 *                the character value to escape
	 * @param buffer
	 *                the string buffer to append the escaped value to
	 */
	static void appendHtmlEscapeChar( char aChar, HtmlStringBuffer buffer ) {
		int index = aChar;

		if ( index < HTML_ENTITIES.length && HTML_ENTITIES[ index ] != null ) {
			buffer.append( HTML_ENTITIES[ index ] );

		} else {
			buffer.append( aChar );
		}
	}

	/**
	 * Append the HTML escaped string for the given character value to the
	 * buffer.
	 *
	 * @param value
	 *                the String value to escape
	 * @param buffer
	 *                the string buffer to append the escaped value to
	 */
	static void appendHtmlEscapeString( String value, HtmlStringBuffer buffer ) {
		char aChar;
		for ( int i = 0, size = value.length( ); i < size; i++ ) {
			aChar = value.charAt( i );
			int index = aChar;

			if ( index < HTML_ENTITIES.length && HTML_ENTITIES[ index ] != null ) {
				buffer.append( HTML_ENTITIES[ index ] );

			} else {
				buffer.append( aChar );
			}
		}
	}

	/**
	 * Return true if the given character requires escaping. The following
	 * characters are escaped: &lt;, &gt;, &quot;, &#039;, &amp;.
	 *
	 * @param aChar
	 *                the character value to test
	 * @return true if the given character requires escaping
	 */
	static boolean requiresEscape( char aChar ) {
		int index = aChar;

		if ( index < XML_ENTITIES.length ) {
			return XML_ENTITIES[ index ] != null;

		} else {
			return false;
		}
	}

	/**
	 * Return true if the given string requires escaping of characters. The
	 * following characters require escaping: &lt;, &gt;, &quot;, &#039;,
	 * &amp;.
	 *
	 * @param value
	 *                the string value to test
	 * @return true if the given string requires escaping of characters
	 */
	static boolean requiresEscape( String value ) {
		if ( value == null ) { return false; }

		int length = value.length( );
		for ( int i = 0; i < length; i++ ) {
			if ( requiresEscape( value.charAt( i ) ) ) { return true; }
		}

		return false;
	}


	/**
	 * Return a HTML escaped string for the given string value.
	 *
	 * @param value
	 *                the string value to escape
	 * @return the HTML escaped string value
	 */
	public static String escapeHtml( String value ) {
		if ( requiresHtmlEscape( value ) ) {

			HtmlStringBuffer buffer = new HtmlStringBuffer( value.length( ) * 2 );

			buffer.appendHtmlEscaped( value );

			return buffer.toString( );

		} else {
			return value;
		}
	}

	/**
	 * Return an escaped string for the given string value. The following
	 * characters are escaped: &lt;, &gt;, &quot;, &#039;, &amp;.
	 *
	 * @param value
	 *                the string value to escape
	 * @return the escaped string value
	 */
	public static String escape( String value ) {
		if ( requiresEscape( value ) ) {

			HtmlStringBuffer buffer = new HtmlStringBuffer( value.length( ) * 2 );

			buffer.appendEscaped( value );

			return buffer.toString( );

		} else {
			return value;
		}
	}

	/**
	 * Return the page resource path from the request. For example:
	 * 
	 * <pre class="codeHtml">
	 * <span class=
	"blue">http://www.mycorp.com/banking/secure/login.htm</span>  ->  <span class
	="red">/secure/login.htm</span>
	 * </pre>
	 *
	 * @param request
	 *                the page servlet request
	 * @return the page resource path from the request
	 */
	public static String getResourcePath( HttpServletRequest request ) {
		// Adapted from VelocityViewServlet.handleRequest() method:

		// If we get here from RequestDispatcher.include(),
		// getServletPath()
		// will return the original (wrong) URI requested. The following
		// special attribute holds the correct path. See section 8.3 of
		// the
		// Servlet 2.3 specification.

		String path = ( String ) request.getAttribute( "javax.servlet.include.servlet_path" );

		// Also take into account the PathInfo stated on
		// SRV.4.4 Request Path Elements.
		String info = ( String ) request.getAttribute( "javax.servlet.include.path_info" );

		if ( path == null ) {
			path = request.getServletPath( );
			info = request.getPathInfo( );
		}

		if ( info != null ) {
			path += info;
		}

		return path;
	}

	/**
	 * Return the requestURI from the request. For example:
	 * 
	 * <pre class="codeHtml">
	 * <span class=
	"blue">http://www.mycorp.com/banking/secure/login.htm</span>  ->  <span class
	="red">/banking/secure/login.htm</span>
	 * </pre>
	 *
	 * @param request
	 *                the page servlet request
	 * @return the requestURI from the request
	 */
	public static String getRequestURI( HttpServletRequest request ) {
		// CLK-334. Adapted from VelocityViewServlet.handleRequest()
		// method:

		// If we get here from RequestDispatcher.include(),
		// getServletPath()
		// will return the original (wrong) URI requested. The following
		// special attribute holds the correct path. See section 8.3 of
		// the
		// Servlet 2.3 specification.

		String requestURI = ( String ) request.getAttribute( "javax.servlet.include.request_uri" );

		if ( requestURI == null ) {
			requestURI = request.getRequestURI( );
		}

		if ( requestURI != null && requestURI.endsWith( ".jsp" ) ) {
			requestURI = StringUtils.replace( requestURI, ".jsp", ".htm" );
		}

		return requestURI;
	}

	public static String toGetterName( String property ) {
		HtmlStringBuffer buffer = new HtmlStringBuffer( property.length( ) + 3 );

		buffer.append( "get" );
		buffer.append( Character.toUpperCase( property.charAt( 0 ) ) );
		buffer.append( property.substring( 1 ) );

		return buffer.toString( );
	}

	/**
	 * Return the is getter method name for the given property name.
	 *
	 * @param property
	 *                the property name
	 * @return the is getter method name for the given property name.
	 */
	public static String toIsGetterName( String property ) {
		HtmlStringBuffer buffer = new HtmlStringBuffer( property.length( ) + 3 );

		buffer.append( "is" );
		buffer.append( Character.toUpperCase( property.charAt( 0 ) ) );
		buffer.append( property.substring( 1 ) );

		return buffer.toString( );
	}

	public static boolean isTemplate(HttpServletRequest request) {
		String path = getResourcePath(request);
		if (path.endsWith(".htm") || path.endsWith(".jsp")) {
			return true;
		}
		return false;
	}

	public static boolean isTemplate(String path,String suffix) {
		if (path.endsWith(suffix)) {
			return true;
		}
		return false;
	}

	public static String getMimeType(String filenameOrSuffix) {
		if (filenameOrSuffix == null) {
			throw new IllegalArgumentException("null filename/extension parameter");
		}

		String ext = filenameOrSuffix;

		int index = filenameOrSuffix.lastIndexOf(".");
		if (index != -1) {
			ext = filenameOrSuffix.substring(index + 1);
		}

		try {
			String filename = Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath()) + "mime-type";
			ResourceBundle bundle = Path.getBundle(filename );

			return bundle.getString(ext.toLowerCase());

		} catch (MissingResourceException mre) {
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String encodeURL( Object value ) {
		if ( value == null ) { throw new IllegalArgumentException( "Null object parameter" ); }

		try {
			return URLEncoder.encode( value.toString( ), "UTF-8" );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public static String decodeURL( Object value ) {
		if ( value == null ) { throw new IllegalArgumentException( "Null object parameter" ); }

		try {
			return URLDecoder.decode( value.toString( ), "UTF-8" );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public static String encodeUrl( Object object, String charset ) {
		if ( object == null ) { throw new IllegalArgumentException( "Null object parameter" ); }

		try {
			if ( org.apache.commons.lang3.StringUtils.isBlank( charset ) ) {
				return URLEncoder.encode( object.toString( ), "UTF-8" );

			} else {
				return URLEncoder.encode( object.toString( ), charset );
			}

		} catch ( UnsupportedEncodingException uee ) {
			throw new RuntimeException( uee );
		}
	}

	public static boolean requiresHtmlEscape( char aChar ) {
		int index = aChar;

		if ( index < HTML_ENTITIES.length ) {
			return HTML_ENTITIES[ index ] != null;

		} else {
			return false;
		}
	}

	public static boolean requiresHtmlEscape( String value ) {
		if ( value == null ) { return false; }

		int length = value.length( );
		for ( int i = 0; i < length; i++ ) {
			if ( requiresHtmlEscape( value.charAt( i ) ) ) { return true; }
		}

		return false;
	}

	public static String escapeHtmlString( String value ) {
		char aChar;
		StringBuffer buffer = new StringBuffer( value.length( ) * 2 );
		for ( int i = 0, size = value.length( ); i < size; i++ ) {
			aChar = value.charAt( i );
			int index = aChar;

			if ( index < HTML_ENTITIES.length && HTML_ENTITIES[ index ] != null ) {
				buffer.append( HTML_ENTITIES[ index ] );

			} else {
				buffer.append( aChar );
			}
		}

		return buffer.toString( );
	}

	public static Cookie getCookie(HttpServletRequest request, String name ) {
		Cookie cookies[] = request.getCookies( );

		if ( cookies == null || name == null || name.length( ) == 0 ) { return null; }

		// Otherwise, we have to do a linear scan for the cookie.
		for ( Cookie cookie : cookies ) {
			if ( cookie.getName( ).equals( name ) ) { return cookie; }
		}

		return null;
	}

	public static Cookie setCookie(HttpServletRequest request, HttpServletResponse response, String name,
								   String value, int maxAge, String path ) {

		Cookie cookie = new Cookie( name, value );
		cookie.setMaxAge( maxAge );
		cookie.setPath( path );
		cookie.setSecure( request.isSecure( ) );
		response.addCookie( cookie );

		return cookie;
	}

	public static String getCookieValue( HttpServletRequest request, String name ) {
		Cookie cookie = getCookie( request, name );

		if ( cookie != null ) { return cookie.getValue( ); }

		return null;
	}

	public static void invalidateCookie( HttpServletRequest request, HttpServletResponse response,
										 String cookieName ) {

		invalidateCookie( request, response, cookieName, "/" );
	}

	public static void invalidateCookie( HttpServletRequest request, HttpServletResponse response,
										 String cookieName, String path ) {

		setCookie( request, response, cookieName, null, 0, path );
	}



}
