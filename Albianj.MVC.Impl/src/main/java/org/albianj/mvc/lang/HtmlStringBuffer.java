/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.albianj.mvc.lang;

import java.util.Map;

/**
 * Provides a HTML element StringBuffer for rendering, automatically escaping
 * string values. HtmlStringBuffer is used by Click controls for HTML rendering.
 * <p/>
 * For example the following code:
 * 
 * <pre class="codeJava">
 * <span class="kw">public</span> String toString() {
 * HtmlStringBuffer buffer = <span class="kw">new</span> HtmlStringBuffer();
 * <p>
 * buffer.elementStart(<span class="st">"input"</span>);
 * buffer.appendAttribute(<span class="st">"type"</span>, <span class=
"st">"text"</span>);
 * buffer.appendAttribute(<span class="st">"name"</span>, getName());
 * buffer.appendAttribute(<span class="st">"value"</span>, getValue());
 * buffer.elementEnd();
 * <p>
 * <span class="kw">return</span> buffer.toString();
 * }
 * </pre>
 * <p>
 * Would render:
 * <p>
 * 
 * <pre class="codeHtml">
 * &lt;input type="text" name="address" value="23 Holt's Street"/&gt;
 * </pre>
 * <p>
 * <h4>Synchronization</h4>
 * <p>
 * To improve performance in Click's thread safe environment this class does not
 * synchronize append operations. Internally this class uses a character buffer
 * adapted from the JDK 1.5 <tt>AbstractStringBuilder</tt>.
 */
public class HtmlStringBuffer {

	// --------------------------------------------------------------
	// Constants

	/**
	 * JavaScript attribute names.
	 */
	static final String[ ] JS_ATTRIBUTES = { "onload", "onunload", "onclick", "ondblclick", "onmousedown",
	                "onmouseup", "onmouseover", "onmousemove", "onmouseout", "onfocus", "onblur", "onkeypress",
	                "onkeydown", "onkeyup", "onsubmit", "onreset", "onselect", "onchange" };

	private StringBuilder characters;

	// -----------------------------------------------------------
	// Constructors

	/**
	 * Create a new HTML StringBuffer with the specified initial capacity.
	 *
	 * @param length
	 *                the initial capacity
	 */
	public HtmlStringBuffer( int length ) {
		characters = new StringBuilder( length );
		// characters = new char[length];
	}

	/**
	 * Create a new HTML StringBuffer with an initial capacity of 128
	 * characters.
	 */
	public HtmlStringBuffer( ) {
		this( 128 );
	}

	// --------------------------------------------------------- Public
	// Methods

	/**
	 * Append the double value to the buffer.
	 *
	 * @param value
	 *                the double value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( double value ) {
		append( String.valueOf( value ) );
		return this;
	}

	/**
	 * Append the char value to the buffer.
	 *
	 * @param value
	 *                the char value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( char value ) {
		characters.append( value );
		return this;
	}

	/**
	 * Append the integer value to the buffer.
	 *
	 * @param value
	 *                the integer value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( int value ) {
		append( String.valueOf( value ) );
		return this;
	}

	/**
	 * Append the long value to the buffer.
	 *
	 * @param value
	 *                the long value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( long value ) {
		append( String.valueOf( value ) );
		return this;
	}

	/**
	 * Append the raw object value of the given object to the buffer.
	 *
	 * @param value
	 *                the object value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( Object value ) {
		characters.append( String.valueOf( value ) );
		return this;
	}

	/**
	 * Append the raw string value of the given object to the buffer.
	 *
	 * @param value
	 *                the string value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer append( String value ) {
		String string = ( value != null ) ? value : "null";
		characters.append( string );
		return this;
	}

	/**
	 * Append the given value to the buffer and escape its value. The
	 * following characters are escaped: &lt;, &gt;, &quot;, &#039;, &amp;.
	 *
	 * @param value
	 *                the object value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if the value is null
	 */
	public HtmlStringBuffer appendEscaped( Object value ) {
		if ( value == null ) { throw new IllegalArgumentException( "Null value parameter" ); }

		String string = value.toString( );

		boolean requiresEscape = false;
		for ( int i = 0, size = string.length( ); i < size; i++ ) {
			if ( HttpHelper.requiresEscape( string.charAt( i ) ) ) {
				requiresEscape = true;
				break;
			}
		}

		if ( requiresEscape ) {
			HttpHelper.appendEscapeString( string, this );

		} else {
			append( string );
		}

		return this;
	}

	/**
	 * Append the given HTML attribute name and value to the string buffer,
	 * and do not escape the attribute value.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * appendAttribute(<span class="st">"size"</span>, 10)  <span class=
	"green">-&gt;</span>  <span class="st">size="10"</span>
	 * </pre>
	 *
	 * @param name
	 *                the HTML attribute name
	 * @param value
	 *                the HTML attribute value
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if name is null
	 */
	public HtmlStringBuffer appendAttribute( String name, Object value ) {
		if ( name == null ) { throw new IllegalArgumentException( "Null name parameter" ); }

		if ( value != null ) {
			append( " " );
			append( name );
			append( "=\"" );
			append( value );
			append( "\"" );
		}

		return this;
	}

	/**
	 * Append the given attribute name and value to the buffer, if the value
	 * is not null.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * appendAttribute(<span class="st">"class"</span>, <span class=
	"st">"required"</span>)  <span class="green">-></span>  <span class=
	"st">class="required"</span>
	 * </pre>
	 * <p>
	 * The attribute value will be escaped. The following characters are
	 * escaped: &lt;, &gt;, &quot;, &#039;, &amp;.
	 * <p/>
	 * If the attribute name is a JavaScript event handler the value will
	 * not be escaped.
	 *
	 * @param name
	 *                the HTML attribute name
	 * @param value
	 *                the object value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if name is null
	 */
	public HtmlStringBuffer appendAttributeEscaped( String name, Object value ) {
		if ( name == null ) { throw new IllegalArgumentException( "Null name parameter" ); }
		if ( value != null ) {
			append( " " );
			append( name );
			append( "=\"" );
			if ( isJavaScriptAttribute( name ) ) {
				append( value );
			} else {
				appendEscaped( value.toString( ) );
			}
			append( "\"" );
		}

		return this;
	}

	/**
	 * Append the given HTML attribute name and value to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * appendAttribute(<span class="st">"size"</span>, 10)  <span class=
	"green">-&gt;</span>  <span class="st">size="10"</span>
	 * </pre>
	 *
	 * @param name
	 *                the HTML attribute name
	 * @param value
	 *                the HTML attribute value
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if name is null
	 */
	public HtmlStringBuffer appendAttribute( String name, int value ) {
		if ( name == null ) { throw new IllegalArgumentException( "Null name parameter" ); }
		append( " " );
		append( name );
		append( "=\"" );
		append( value );
		append( "\"" );
		return this;
	}

	/**
	 * Append the HTML "disabled" attribute to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * appendAttributeDisabled()  <span class="green">-></span>  <span class
	="st">disabled="disabled"</span>
	 * </pre>
	 *
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer appendAttributeDisabled( ) {
		append( " disabled=\"disabled\"" );
		return this;
	}

	/**
	 * Append the HTML "readonly" attribute to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * appendAttributeReadonly()  <span class="green">-></span>  <span class
	="st">readonly="readonly"</span>
	 * </pre>
	 *
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer appendAttributeReadonly( ) {
		append( " readonly=\"readonly\"" );
		return this;
	}

	/**
	 * Append the given map of attribute names and values to the string
	 * buffer.
	 *
	 * @param attributes
	 *                the map of attribute names and values
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if attributes is null
	 */
	public HtmlStringBuffer appendAttributes( Map< String, String > attributes ) {
		if ( attributes == null ) { throw new IllegalArgumentException( "Null attributes parameter" ); }
		for ( Map.Entry< String, String > entry : attributes.entrySet( ) ) {
			String name = entry.getKey( );
			if ( !name.equals( "id" ) ) {
				appendAttributeEscaped( name, entry.getValue( ) );
			}
		}

		return this;
	}

	/**
	 * Append the given map of CSS style name and value pairs as a style
	 * attribute to the string buffer.
	 *
	 * @param attributes
	 *                the map of CSS style names and values
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if attributes is null
	 */
	public HtmlStringBuffer appendStyleAttributes( Map< String, String > attributes ) {
		if ( attributes == null ) { throw new IllegalArgumentException( "Null attributes parameter" ); }

		if ( !attributes.isEmpty( ) ) {
			append( " style=\"" );

			for ( Map.Entry< String, String > entry : attributes.entrySet( ) ) {
				append( entry.getKey( ) );
				append( ":" );
				append( entry.getValue( ) );
				append( ";" );
			}

			append( "\"" );
		}

		return this;
	}

	/**
	 * Append a HTML element end to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * elementEnd(<span class="st">"textarea"</span>)  <span class=
	"green">-></span>  <span class="st">&lt;/textarea&gt;</span>
	 * </pre>
	 *
	 * @param tag
	 *                the HTML element name to end
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if name is null
	 */
	public HtmlStringBuffer elementEnd( String tag ) {
		if ( tag == null ) { throw new IllegalArgumentException( "Null tag parameter" ); }
		append( "</" );
		append( tag );
		append( ">" );

		return this;
	}

	/**
	 * Append a HTML element end to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * closeTag()  <span class="green">-></span>  <span class=
	"st">&gt;</span>
	 * </pre>
	 *
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer closeTag( ) {
		append( ">" );

		return this;
	}

	/**
	 * Append a HTML element end to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * elementEnd()  <span class="green">-></span>  <span class=
	"st">/&gt;</span>
	 * </pre>
	 *
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer elementEnd( ) {
		append( "/>" );

		return this;
	}

	/**
	 * Append a HTML element start to the string buffer.
	 * <p/>
	 * For example:
	 * 
	 * <pre class="javaCode">
	 * elementStart(<span class="st">"input"</span>)  <span class=
	"green">-></span>  <span class="st">&lt;input</span>
	 * </pre>
	 *
	 * @param tag
	 *                the HTML element name to start
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 */
	public HtmlStringBuffer elementStart( String tag ) {
		append( "<" );
		append( tag );

		return this;
	}

	/**
	 * Return true if the given attribute name is a JavaScript attribute, or
	 * false otherwise.
	 *
	 * @param name
	 *                the HTML attribute name to test
	 * @return true if the HTML attribute is a JavaScript attribute
	 */
	public boolean isJavaScriptAttribute( String name ) {
		if ( name.length( ) < 6 || name.length( ) > 11 ) { return false; }

		if ( !name.startsWith( "on" ) ) { return false; }

		for ( String jsAttribute : JS_ATTRIBUTES ) {
			if ( jsAttribute.equalsIgnoreCase( name ) ) { return true; }
		}

		return false;
	}

	/**
	 * Return the length of the string buffer.
	 *
	 * @return the length of the string buffer
	 */
	public int length( ) {
		return characters.length( );
	}

	/**
	 * @return a string representation of the string buffer
	 * @see Object#toString()
	 */
	@Override
	public String toString( ) {
		return characters.toString( );
	}

	// Private Package Methods
	// ------------------------------------------------

	/**
	 * Append the given value to the buffer and HTML escape its value.
	 *
	 * @param value
	 *                the object value to append
	 * @return a reference to this <tt>HtmlStringBuffer</tt> object
	 * @throws IllegalArgumentException
	 *                 if the value is null
	 */
	HtmlStringBuffer appendHtmlEscaped( Object value ) {
		if ( value == null ) { throw new IllegalArgumentException( "Null value parameter" ); }

		String string = value.toString( );

		boolean requiresEscape = false;
		for ( int i = 0, size = string.length( ); i < size; i++ ) {
			if ( HttpHelper.requiresHtmlEscape( string.charAt( i ) ) ) {
				requiresEscape = true;
				break;
			}
		}

		if ( requiresEscape ) {
			HttpHelper.appendHtmlEscapeString( string, this );

		} else {
			append( value );
		}

		return this;
	}
}
