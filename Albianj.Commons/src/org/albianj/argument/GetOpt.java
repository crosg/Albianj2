/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.argument;

/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: GetOpt.java,v 1.2.4.1 2005/08/31 11:46:04 pvedula Exp $
 */

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * GetOpt is a Java equivalent to the C getopt() library function
 * discussed in man page getopt(3C). It provides command line
 * parsing for Java applications. It supports the most rules of the
 * command line standard (see man page intro(1)) including stacked
 * options such as '-sxm' (which is equivalent to -s -x -m); it
 * handles special '--' option that signifies the end of options.
 * Additionally this implementation of getopt will check for
 * mandatory arguments to options such as in the case of
 * '-d <file>' it will throw a MissingOptArgException if the
 * option argument '<file>' is not included on the commandline.
 * getopt(3C) does not check for this.
 *
 * @author G Todd Miller
 */
public class GetOpt {

    public static final String ILLEGAL_CMDLINE_OPTION_ERR = "ILLEGAL_CMDLINE_OPTION_ERR";

    public GetOpt(String[] args, String optString) {
        theOptions = new ArrayList();
        int currOptIndex = 0;
        theCmdArgs = new ArrayList();
        theOptionMatcher = new OptionMatcher(optString);
        // fill in the options list
        for (int i = 0; i < args.length; i++) {
            String token = args[i];
            int tokenLength = token.length();
            if (token.equals("--")) {         // end of opts
                currOptIndex = i + 1;         // set index of first operand
                break;                      // end of options
            } else if (token.startsWith("-") && tokenLength == 2) {
                // simple option token such as '-s' found
                theOptions.add(new Option(token.charAt(1)));
            } else if (token.startsWith("-") && tokenLength > 2) {
                // stacked options found, such as '-shm'
                // iterate thru the tokens after the dash and
                // add them to theOptions list
                for (int j = 1; j < tokenLength; j++) {
                    theOptions.add(new Option(token.charAt(j)));
                }
            } else if (!token.startsWith("-")) {
                // case 1- there are not options stored yet therefore
                // this must be an command argument, not an option argument
                if (theOptions.size() == 0) {
                    currOptIndex = i;
                    break;              // stop processing options
                } else {
                    // case 2-
                    // there are options stored, check to see if
                    // this arg belong to the last arg stored
                    int indexoflast = 0;
                    indexoflast = theOptions.size() - 1;
                    Option op = (Option) theOptions.get(indexoflast);
                    char opLetter = op.getArgLetter();
                    if (!op.hasArg() && theOptionMatcher.hasArg(opLetter)) {
                        op.setArg(token);
                    } else {
                        // case 3 -
                        // the last option stored does not take
                        // an argument, so again, this argument
                        // must be a command argument, not
                        // an option argument
                        currOptIndex = i;
                        break;                  // end of options
                    }
                }
            }// end option does not start with "-"
        } // end for args loop

        //  attach an iterator to list of options
        theOptionsIterator = theOptions.listIterator();

        // options are done, now fill out cmd arg list with remaining args
        for (int i = currOptIndex; i < args.length; i++) {
            String token = args[i];
            theCmdArgs.add(token);
        }
    }


    /**
     * debugging routine to print out all options collected
     */
    public void printOptions() {
        for (ListIterator it = theOptions.listIterator(); it.hasNext(); ) {
            Option opt = (Option) it.next();
            System.out.print("OPT =" + opt.getArgLetter());
            String arg = opt.getArgument();
            if (arg != null) {
                System.out.print(" " + arg);
            }
            System.out.println();
        }
    }

    /**
     * gets the next option found in the commandline. Distinguishes
     * between two bad cases, one case is when an illegal option
     * is found, and then other case is when an option takes an
     * argument but no argument was found for that option.
     * If the option found was not declared in the optString, then
     * an IllegalArgumentException will be thrown (case 1).
     * If the next option found has been declared to take an argument,
     * and no such argument exists, then a MissingOptArgException
     * is thrown (case 2).
     *
     * @return int - the next option found.
     * @throws IllegalArgumentException, MissingOptArgException.
     */
    public int getNextOption() throws IllegalArgumentException {
        int retval = -1;
        if (theOptionsIterator.hasNext()) {
            theCurrentOption = (Option) theOptionsIterator.next();
            char c = theCurrentOption.getArgLetter();
            boolean shouldHaveArg = theOptionMatcher.hasArg(c);
            String arg = theCurrentOption.getArgument();
            if (!theOptionMatcher.match(c)) {
//                ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
//                                            new Character(c));
                throw (new IllegalArgumentException(String.format("%s : %s", ILLEGAL_CMDLINE_OPTION_ERR, new Character(c))));
            } else if (shouldHaveArg && (arg == null)) {
                throw (new IllegalArgumentException(String.format("%s : %s", ILLEGAL_CMDLINE_OPTION_ERR, new Character(c))));
            }
            retval = c;
        }
        return retval;
    }

    /**
     * gets the argument for the current parsed option. For example,
     * in case of '-d <file>', if current option parsed is 'd' then
     * getOptionArg() would return '<file>'.
     *
     * @return String - argument for current parsed option.
     */
    public String getOptionArg() {
        String retval = null;
        String tmp = theCurrentOption.getArgument();
        char c = theCurrentOption.getArgLetter();
        if (theOptionMatcher.hasArg(c)) {
            retval = tmp;
        }
        return retval;
    }

    /**
     * gets list of the commandline arguments. For example, in command
     * such as 'cmd -s -d file file2 file3 file4'  with the usage
     * 'cmd [-s] [-d <file>] <file>...', getCmdArgs() would return
     * the list {file2, file3, file4}.
     *
     * @return String[] - list of command arguments that may appear
     * after options and option arguments.
     * @params none
     */
    public String[] getCmdArgs() {
        String[] retval = new String[theCmdArgs.size()];
        int i = 0;
        for (ListIterator it = theCmdArgs.listIterator(); it.hasNext(); ) {
            retval[i++] = (String) it.next();
        }
        return retval;
    }


    private Option theCurrentOption = null;
    private ListIterator theOptionsIterator;
    private List theOptions = null;
    private List theCmdArgs = null;
    private OptionMatcher theOptionMatcher = null;

    ///////////////////////////////////////////////////////////
    //
    //   Inner Classes
    //
    ///////////////////////////////////////////////////////////

    // inner class to model an option
    class Option {
        private char theArgLetter;
        private String theArgument = null;

        public Option(char argLetter) {
            theArgLetter = argLetter;
        }

        public void setArg(String arg) {
            theArgument = arg;
        }

        public boolean hasArg() {
            return (theArgument != null);
        }

        public char getArgLetter() {
            return theArgLetter;
        }

        public String getArgument() {
            return theArgument;
        }
    } // end class Option


    // inner class to query optString for a possible option match,
    // and whether or not a given legal option takes an argument.
    //
    class OptionMatcher {
        public OptionMatcher(String optString) {
            theOptString = optString;
        }

        public boolean match(char c) {
            boolean retval = false;
            if (theOptString.indexOf(c) != -1) {
                retval = true;
            }
            return retval;
        }

        public boolean hasArg(char c) {
            boolean retval = false;
            int index = theOptString.indexOf(c) + 1;
            if (index == theOptString.length()) {
                // reached end of theOptString
                retval = false;
            } else if (theOptString.charAt(index) == ':') {
                retval = true;
            }
            return retval;
        }

        private String theOptString = null;
    } // end class OptionMatcher
}// end class GetOpt
