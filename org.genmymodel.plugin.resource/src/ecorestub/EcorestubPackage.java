/**
 */
package ecorestub;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see ecorestub.EcorestubFactory
 * @model kind="package"
 * @generated
 */
public interface EcorestubPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ecorestub";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ecore.abstract.stub";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.genmymodel.ecore.stub";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EcorestubPackage eINSTANCE = ecorestub.impl.EcorestubPackageImpl.init();

	/**
	 * The meta object id for the '{@link ecorestub.impl.EModelElementStubImpl <em>EModel Element Stub</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ecorestub.impl.EModelElementStubImpl
	 * @see ecorestub.impl.EcorestubPackageImpl#getEModelElementStub()
	 * @generated
	 */
	int EMODEL_ELEMENT_STUB = 0;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMODEL_ELEMENT_STUB__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The number of structural features of the '<em>EModel Element Stub</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMODEL_ELEMENT_STUB_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Get EAnnotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMODEL_ELEMENT_STUB___GET_EANNOTATION__STRING = EcorePackage.EMODEL_ELEMENT___GET_EANNOTATION__STRING;

	/**
	 * The number of operations of the '<em>EModel Element Stub</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMODEL_ELEMENT_STUB_OPERATION_COUNT = EcorePackage.EMODEL_ELEMENT_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link ecorestub.EModelElementStub <em>EModel Element Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EModel Element Stub</em>'.
	 * @see ecorestub.EModelElementStub
	 * @generated
	 */
	EClass getEModelElementStub();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EcorestubFactory getEcorestubFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link ecorestub.impl.EModelElementStubImpl <em>EModel Element Stub</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ecorestub.impl.EModelElementStubImpl
		 * @see ecorestub.impl.EcorestubPackageImpl#getEModelElementStub()
		 * @generated
		 */
		EClass EMODEL_ELEMENT_STUB = eINSTANCE.getEModelElementStub();

	}

} //EcorestubPackage
