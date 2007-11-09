/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.ext.typeinfo;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.test.CA;
import com.google.gwt.core.ext.typeinfo.test.CB;
import com.google.gwt.core.ext.typeinfo.test.MyCustomList;
import com.google.gwt.core.ext.typeinfo.test.MyList;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;

import junit.framework.TestCase;

/**
 * Tests for {@link JArrayType}.
 */
public class JArrayTypeTest extends TestCase {
  /*
   * Returns int[][]
   */
  private static JArrayType getIntArray(TypeOracle oracle) {
    return oracle.getArrayType(getIntVector(oracle));
  }
  /*
   * Returns int[].
   */
  private static JArrayType getIntVector(TypeOracle oracle) {
    return oracle.getArrayType(JPrimitiveType.INT);
  }

  /*
   * Returns Object[][]
   */
  private static JArrayType getObjectArray(TypeOracle oracle) {
    return oracle.getArrayType(getObjectVector(oracle));
  }

  /*
   * Returns Object[].
   */
  private static JArrayType getObjectVector(TypeOracle oracle) {
    return oracle.getArrayType(oracle.getJavaLangObject());
  }

  private final boolean logToConsole = false;

  private final ModuleContext moduleContext = new ModuleContext(logToConsole
      ? new PrintWriterTreeLogger() : TreeLogger.NULL,
      "com.google.gwt.core.ext.typeinfo.TypeOracleTest");

  public JArrayTypeTest() throws UnableToCompleteException {
  }

  public void testGetSubtypes() throws NotFoundException {
    JArrayType testArrayType = getTestArrayType();

    JArrayType[] arraySubtypes = testArrayType.getSubtypes();
    assertEquals(2, arraySubtypes.length);

    for (int i = 0; i < arraySubtypes.length; ++i) {
      assertTrue(testArrayType.isAssignableFrom(arraySubtypes[i]));
    }
  }

  public void testGetSuperclass() throws NotFoundException {
    TypeOracle oracle = moduleContext.getOracle();
    JArrayType intVector = getIntVector(oracle);
    JArrayType intArray = getIntArray(oracle);
    JArrayType objVector = getObjectVector(oracle);
    JArrayType objArray = getObjectArray(oracle);

    // Check that superclass of int[][] is Object[]
    assertEquals(objVector, intArray.getSuperclass());

    // Check that superclass of int[] is Object
    assertEquals(oracle.getJavaLangObject(), intVector.getSuperclass());

    // CA
    JClassType caType = oracle.getType(CA.class.getCanonicalName());

    // CA[]
    JArrayType caVector = oracle.getArrayType(caType);

    // CA[][]
    JArrayType caArray = oracle.getArrayType(caVector);

    // CB
    JClassType cbType = oracle.getType(CB.class.getCanonicalName());

    // CB[]
    JArrayType cbVector = oracle.getArrayType(cbType);

    // CB[][]
    JArrayType cbArray = oracle.getArrayType(cbVector);

    // Check that CB[][] has a supertype of CA[][]
    JClassType cbArraySuper = cbArray.getSuperclass();
    assertEquals(caArray, cbArraySuper);

    // Check that CA[][] has a supertype of Object[][]
    JClassType cbArraySuperSuper = cbArraySuper.getSuperclass();
    assertEquals(objArray, cbArraySuperSuper);

    // Check that Object[][] has supertype of Object[]
    JClassType cbArraySuperSuperSuper = cbArraySuperSuper.getSuperclass();
    assertEquals(objVector, cbArraySuperSuperSuper);

    // Check that Object[] has supertype of Object
    JClassType cbArraySuperSuperSuperSuper = cbArraySuperSuperSuper.getSuperclass();
    assertEquals(oracle.getJavaLangObject(), cbArraySuperSuperSuperSuper);
  }

  public void testIsAssignableFrom() throws NotFoundException {
    TypeOracle oracle = moduleContext.getOracle();
    
    JArrayType intVector = getIntVector(oracle);
    JArrayType intArray = getIntArray(oracle);
    JArrayType objVector = getObjectVector(oracle);
    JArrayType objArray = getObjectArray(oracle);

    // Object[] is not assignable from int[]
    assertFalse(objVector.isAssignableFrom(intVector));
    assertFalse(intVector.isAssignableFrom(objVector));

    // Object[] is assignable from int[][]
    assertTrue(objVector.isAssignableFrom(intArray));

    // Object[] is assignable from Object[][]
    assertTrue(objVector.isAssignableFrom(objArray));

    // int[] is assignable from int[]
    assertTrue(intVector.isAssignableFrom(intVector));
    
    // int[] is assignable from int[][]
    assertFalse(intArray.isAssignableFrom(intVector));
    
    JClassType testSubtype = oracle.getType(MyCustomList.class.getName());
    
    // MyCustomList[]
    JArrayType testArraySubtype = oracle.getArrayType(testSubtype);

    // MyList[] is assignable from MyCustomList[]
    assertTrue(getTestArrayType().isAssignableFrom(testArraySubtype));

    // MyCustomList[]
    JArrayType testVectorSubtype = oracle.getArrayType(testArraySubtype);
    
    // MyList[] is not assignable from MyCustomList[][]    
    assertFalse(getTestArrayType().isAssignableFrom(testVectorSubtype));

    // MyCustomList[] is not assignable from MyList[]
    assertFalse(testArraySubtype.isAssignableFrom(getTestArrayType()));
  }

  public void testIsAssignableTo() throws NotFoundException {
    TypeOracle oracle = moduleContext.getOracle();
    JClassType testSubtype = oracle.getType(MyCustomList.class.getName());
    JArrayType testArraySubtype = oracle.getArrayType(testSubtype);

    // MyCustomList[] is assignable to MyList[]
    assertTrue(testArraySubtype.isAssignableTo(getTestArrayType()));
    
    // MyCustomList[] is assignable to Object
    assertTrue(testArraySubtype.isAssignableTo(oracle.getJavaLangObject()));
    
    // MyList[] is not assignable to MyCustomList[]
    assertFalse(getTestArrayType().isAssignableTo(testArraySubtype));
    
    // MyList[] is assignable to MyCustomList[]
    assertFalse(getTestArrayType().isAssignableTo(testArraySubtype));

  }

  private JArrayType getTestArrayType() throws NotFoundException {
    TypeOracle oracle = moduleContext.getOracle();
    JGenericType genericComponentType = oracle.getType(MyList.class.getName()).isGenericType();

    return oracle.getArrayType(genericComponentType);
  }
}
