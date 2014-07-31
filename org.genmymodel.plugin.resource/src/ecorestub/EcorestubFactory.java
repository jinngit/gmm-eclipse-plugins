/**
 */
package ecorestub;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see ecorestub.EcorestubPackage
 * @generated
 */
public interface EcorestubFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EcorestubFactory eINSTANCE = ecorestub.impl.EcorestubFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>EModel Element Stub</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EModel Element Stub</em>'.
	 * @generated
	 */
	EModelElementStub createEModelElementStub();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EcorestubPackage getEcorestubPackage();

} //EcorestubFactory
